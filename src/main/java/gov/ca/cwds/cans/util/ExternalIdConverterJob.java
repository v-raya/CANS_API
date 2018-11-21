package gov.ca.cwds.cans.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import liquibase.Liquibase;
import liquibase.change.Change;
import liquibase.change.ChangeFactory;
import liquibase.change.ColumnConfig;
import liquibase.change.core.UpdateDataChange;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.filter.ChangeSetFilterResult;
import liquibase.changelog.filter.ShouldRunChangeSetFilter;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author CWDS TPT-2 Team
 */
@Slf4j
public class ExternalIdConverterJob implements DbUpgradeJob {

  // Changelog metadata
  private static final String JOB_ID = "cans_1.0_to_1.1_conversion";
  private static final String JOB_AUTHOR = "TPT-2 Team";
  private static final String JOB_FILENAME = "ExternalIdConverterJob";
  private static final String JOB_DESCRIPTION = "Converts UIID to Base62 identifiers for Clients and assessments";

  private static final String CLIENT_TABLE_NAME = "person";
  private static final String CLIENT_ID_FIELD_NAME = "id";
  private static final String CLIENT_EXTERNAL_ID_FIELD_NAME = "external_id";
  public static final String LOG_MESSAGE_SEPARATOR = "=============================================================";

  private final String dbUrl;
  private final String dbUser;
  private final String dbPassword;
  private final String schemaName;

  public ExternalIdConverterJob(
      String url,
      String user,
      String password,
      String dbSchema) {
    dbUrl = url;
    dbUser = user;
    dbPassword = password;
    schemaName = dbSchema;
  }

  @Override
  public void run() {
    try {
      DatabaseChangeLog changeLog = new DatabaseChangeLog();
      ChangeSet changeSet = prepareChangeSet(changeLog);
      if (isAccepted(changeSet)) {
        updateExternalIds(changeSet);
      } else {
        log.info("ExternalIds will not be updated, changes are already there.");
      }
    } catch (UpgradeDbException e) {
      log.error(e.getMessage(), e);
    }
  }

  private boolean isAccepted(ChangeSet changeSet) throws UpgradeDbException {
    try {
      ShouldRunChangeSetFilter filter = new ShouldRunChangeSetFilter(getDatabase());
      ChangeSetFilterResult accepts = filter.accepts(changeSet);
      return accepts.isAccepted();
    } catch (Exception e) {
      throw new UpgradeDbException(e.getMessage(), e);
    }
  }

  private void updateExternalIds(ChangeSet changeSet) throws UpgradeDbException {
    log.info("============ Preparing External Id changes =============");
    List<Change> changes = collectChangesForAssessmentCaseId(
        collectChangesForClientExternalId(new Builder<>())).build();
    log.info(LOG_MESSAGE_SEPARATOR);
    log.info("============ Changes size: " + changes.size());
    log.info(LOG_MESSAGE_SEPARATOR);

    if (changes.isEmpty()) {
      log.info("============ No data found to change");
      log.info(LOG_MESSAGE_SEPARATOR);
      return;
    }

    log.info("============ Finish of Preparing External Id changes =============");

    // Update
    try {
      changes.forEach(changeSet::addChange);
      Database database = getDatabase();
      ChangeLogParameters changeLogParameters = new ChangeLogParameters(database);
      DatabaseChangeLog changeLog = changeSet.getChangeLog();
      changeLog.setChangeLogParameters(changeLogParameters);
      Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(), database);
      liquibase.update((String)null);
    } catch (Exception e) {
      throw new UpgradeDbException(e.getMessage(), e);
    }
  }

  private ChangeSet prepareChangeSet(DatabaseChangeLog changeLog) {
    ChangeSet changeSet = new ChangeSet(JOB_ID, JOB_AUTHOR, false, false, JOB_FILENAME,
        null, null, changeLog);
    changeSet.setComments(JOB_DESCRIPTION);
    changeSet.addValidCheckSum("1:*");
    changeLog.addChangeSet(changeSet);
    return changeSet;
  }

  private Builder<Change> collectChangesForClientExternalId(Builder<Change> changesBuilder)
      throws UpgradeDbException {
    Statement statement = null;
    ResultSet resultSet = null;
    try (Connection conn = getConnection()) {
      statement = conn.createStatement();
      resultSet = statement.executeQuery(
          "SELECT " + CLIENT_ID_FIELD_NAME + ", " + CLIENT_EXTERNAL_ID_FIELD_NAME + " FROM "
              + CLIENT_TABLE_NAME);

      while (resultSet.next()) {
        Long clientId = resultSet.getLong(CLIENT_ID_FIELD_NAME);
        String clientExternalId = resultSet.getString(CLIENT_EXTERNAL_ID_FIELD_NAME);
        if (clientExternalId != null && clientExternalId.length() > 10) {
            String base62Key = convertToBase62(clientExternalId);
            log.info("{} => {}", clientExternalId, base62Key);
            changesBuilder.add(buildUpdateClientExternalIdChange(clientId, base62Key));
        }
      }

    } catch (Exception e) {
      throw new UpgradeDbException(e.getMessage(), e);
    } finally {
      closeCloseable(resultSet);
      closeCloseable(statement);
    }
    return changesBuilder;
  }

  private Builder<Change> collectChangesForAssessmentCaseId(Builder<Change> changesBuilder) {
    return changesBuilder;
  }

  private Database getDatabase() throws DatabaseException, SQLException {
    Database database = DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(
            new JdbcConnection(DriverManager.getConnection(dbUrl, dbUser, dbPassword)));
    if (StringUtils.isNotEmpty(schemaName)) {
      database.setDefaultSchemaName(schemaName);
    }
    return database;
  }

  private String convertToBase62(String clientExternalId) {
    try {
      return CmsKeyIdGenerator.getKeyFromUIIdentifier(clientExternalId);
    } catch (Exception e) {
      log.warn("Unable to convert person external_id [{}]", clientExternalId);
    }
    return clientExternalId;
  }

  private Change buildUpdateClientExternalIdChange(Long id, String newValue) {
    ColumnConfig column = new ColumnConfig();
    column.setName(CLIENT_EXTERNAL_ID_FIELD_NAME);
    column.setValue(newValue);
    UpdateDataChange change = (UpdateDataChange) ChangeFactory.getInstance().create("update");
    change.setTableName(CLIENT_TABLE_NAME);
    change.setColumns(new ImmutableList.Builder<ColumnConfig>().add(column).build());
    change.setWhere("id=" + id);
    return change;
  }

  private Connection getConnection() throws SQLException {
    Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    conn.setSchema(schemaName);
    return conn;
  }

  private void closeCloseable(AutoCloseable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}

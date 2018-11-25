package gov.ca.cwds.cans.util;

import static gov.ca.cwds.cans.util.DbUpgradeConstants.CLIENT_EXTERNAL_ID_FIELD_NAME;
import static gov.ca.cwds.cans.util.DbUpgradeConstants.CLIENT_ID_FIELD_NAME;
import static gov.ca.cwds.cans.util.DbUpgradeConstants.CLIENT_TABLE_NAME;

import com.google.common.collect.ImmutableList;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

/** @author CWDS TPT-2 Team */
@Slf4j
public class ExternalIdConverterJob implements DbUpgradeJob, ConnectionProvider {

  // Changelog metadata
  private static final String JOB_ID = "cans_1.0_to_1.1_conversion";
  private static final String JOB_AUTHOR = "TPT-2 Team";
  private static final String JOB_FILENAME = "ExternalIdConverterJob";
  private static final String JOB_DESCRIPTION =
      "Converts UIID to Base62 identifiers for Clients and assessments";

  public static final String LOG_MESSAGE_SEPARATOR =
      "=============================================================";

  private final String dbUrl;
  private final String dbUser;
  private final String dbPassword;
  private final String schemaName;

  public ExternalIdConverterJob(String url, String user, String password, String dbSchema) {
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
        fillChangeSet(changeSet);
        update(changeSet);
      } else {
        log.info("ExternalIds will not be updated, changes are already there.");
      }
    } catch (UpgradeDbException e) {
      log.error(e.getMessage(), e);
    }
  }

  private boolean isAccepted(ChangeSet changeSet) {
    try {
      ShouldRunChangeSetFilter filter = new ShouldRunChangeSetFilter(getDatabase());
      ChangeSetFilterResult accepts = filter.accepts(changeSet);
      return accepts.isAccepted();
    } catch (Exception e) {
      throw new UpgradeDbException(e.getMessage(), e);
    }
  }

  private void fillChangeSet(ChangeSet changeSet) {
    log.info("============ Preparing External Id changes =============");
    final ChangesBuilder changesBuilder = new ChangesBuilder();
    changesBuilder.addChangesProvider(() -> collectChangesForClientExternalId(changesBuilder));
    changesBuilder.addChangesProvider(() -> collectChangesForAssessmentCaseId(changesBuilder));
    changesBuilder.addValidator(new ClientExternalIdValidator(this));
    List<Change> changes = changesBuilder.build();
    log.info(LOG_MESSAGE_SEPARATOR);
    log.info("=== Success Changes count: {}", changes.size());
    log.info("=== Errors count: {}", changesBuilder.getErrors().size());
    log.info(LOG_MESSAGE_SEPARATOR);
    if (changes.isEmpty()) {
      log.info("=== No data found to change");
      return;
    }
    log.info("=== Finish of Preparing External Id changes");
    printErrors(changesBuilder);
    changes.forEach(changeSet::addChange);
  }

  private void update(ChangeSet changeSet) {
    try {
      Database database = getDatabase();
      ChangeLogParameters changeLogParameters = new ChangeLogParameters(database);
      DatabaseChangeLog changeLog = changeSet.getChangeLog();
      changeLog.setChangeLogParameters(changeLogParameters);
      Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(), database);
      liquibase.update((String) null);
    } catch (Exception e) {
      throw new UpgradeDbException(e.getMessage(), e);
    }
  }

  private void printErrors(ChangesBuilder changesBuilder) {
    int errorsCount = changesBuilder.getErrors().size();
    if (errorsCount > 0) {
      changesBuilder.printErrors();
    }
  }

  private ChangeSet prepareChangeSet(DatabaseChangeLog changeLog) {
    ChangeSet changeSet =
        new ChangeSet(JOB_ID, JOB_AUTHOR, false, false, JOB_FILENAME, null, null, changeLog);
    changeSet.setComments(JOB_DESCRIPTION);
    changeSet.addValidCheckSum("1:*");
    changeLog.addChangeSet(changeSet);
    return changeSet;
  }

  private List<Change> collectChangesForClientExternalId(ChangesBuilder changesBuilder) {
    List<Change> changes = new LinkedList<>();
    getClientIdExternalIdMap()
        .forEach(
            (id, extId) -> {
              if (extId != null && extId.matches("\\d{4}-\\d{4}-\\d{4}-\\d{7}")) {
                try {
                  String base62Key = convertToBase62(extId);
                  log.info("Person external_id: {} => {}", extId, base62Key);
                  changes.add(buildUpdateClientExternalIdChange(id, base62Key));
                } catch (Exception e) {
                  changesBuilder.addError(
                      "Unable to convert person external_id [" + extId + "]", e);
                }
              }
            });
    return changes;
  }

  private Map<Long, String> getClientIdExternalIdMap() {
    Map<Long, String> clientExternalIdMap;
    try (Connection conn = getConnection()) {
      try (Statement statement = conn.createStatement()) {
        try (ResultSet resultSet =
            statement.executeQuery(
                "SELECT "
                    + CLIENT_ID_FIELD_NAME
                    + ", "
                    + CLIENT_EXTERNAL_ID_FIELD_NAME
                    + " FROM "
                    + CLIENT_TABLE_NAME)) {
          clientExternalIdMap = new HashMap<>(resultSet.getFetchSize());
          while (resultSet.next()) {
            Long clientId = resultSet.getLong(CLIENT_ID_FIELD_NAME);
            String clientExternalId = resultSet.getString(CLIENT_EXTERNAL_ID_FIELD_NAME);
            clientExternalIdMap.put(clientId, clientExternalId);
          }
        }
      }
    } catch (Exception e) {
      throw new UpgradeDbException(e.getMessage(), e);
    }
    return clientExternalIdMap;
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

  private List<Change> collectChangesForAssessmentCaseId(final ChangesBuilder changesBuilder) {
    List<Change> changes = new LinkedList<>();
    getAssessmentCaseExternalIdMap()
        .forEach(
            (assessmentId, caseExtId) -> {
              if (caseExtId != null && caseExtId.matches("\\d{4}-\\d{3}-\\d{4}-\\d{8}")) {
                try {
                  String base62Key = convertToBase62(caseExtId);
                  log.info("case external_id: {} => {}", caseExtId, base62Key);
                  changes.add(buildUpdateAssessmentCaseExternalIdChange(assessmentId, base62Key));
                } catch (Exception e) {
                  changesBuilder.addError(
                      "Unable to convert case external_id [" + caseExtId + "]", e);
                }
              }
            });
    return changes;
  }

  private Map<Long, String> getAssessmentCaseExternalIdMap() {
    Map<Long, String> assessmentCaseExternalIdMap;
    Statement statement = null;
    ResultSet resultSet = null;
    try (Connection conn = getConnection()) {
      statement = conn.createStatement();
      resultSet =
          statement.executeQuery(
              "SELECT a.id as id, c.external_id as case_ext_id "
                  + "FROM assessment AS a "
                  + "JOIN cases AS c on a.case_id = c.id "
                  + "WHERE a.case_id NOTNULL");
      assessmentCaseExternalIdMap = new HashMap<>();
      while (resultSet.next()) {
        Long assessmentId = resultSet.getLong("id");
        String caseExternalId = resultSet.getString("case_ext_id");
        if (caseExternalId != null) {
          assessmentCaseExternalIdMap.put(assessmentId, caseExternalId);
        }
      }
    } catch (Exception e) {
      throw new UpgradeDbException(e.getMessage(), e);
    } finally {
      closeCloseable(resultSet);
      closeCloseable(statement);
    }
    return assessmentCaseExternalIdMap;
  }

  private Change buildUpdateAssessmentCaseExternalIdChange(Long id, String newValue) {

    ColumnConfig serviceSourceIdColumn = new ColumnConfig();
    serviceSourceIdColumn.setName("service_source_id");
    serviceSourceIdColumn.setValue(newValue);

    ColumnConfig serviceSourceColumn = new ColumnConfig();
    serviceSourceColumn.setName("service_source");
    serviceSourceColumn.setType(schemaName + ".service_source");
    serviceSourceColumn.setValue("CASE");

    UpdateDataChange change = (UpdateDataChange) ChangeFactory.getInstance().create("update");
    change.setTableName("assessment");
    change.setColumns(
        new ImmutableList.Builder<ColumnConfig>()
            .add(serviceSourceIdColumn)
            .add(serviceSourceColumn)
            .build());
    change.setWhere("id=" + id);
    return change;
  }

  private Database getDatabase() throws DatabaseException, SQLException {
    Database database =
        DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(
                new JdbcConnection(DriverManager.getConnection(dbUrl, dbUser, dbPassword)));
    if (StringUtils.isNotEmpty(schemaName)) {
      database.setDefaultSchemaName(schemaName);
    }
    return database;
  }

  private String convertToBase62(String clientExternalId) {
    return CmsKeyIdGenerator.getKeyFromUIIdentifier(clientExternalId);
  }

  @Override
  public Connection getConnection() throws SQLException {
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

package gov.ca.cwds.cans.util;

import gov.ca.cwds.cans.exception.DaoException;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/** @author CWDS TPT-2 Team */
@Slf4j
public class UpgradeDbLiquibaseJob implements DbUpgradeJob {

  private final String dbUrl;
  private final String dbUser;
  private final String dbPassword;
  private final String liquibaseScript;
  private final String dbSchema;

  public UpgradeDbLiquibaseJob(
      String url, String user, String password, String liquibaseScript, String dbSchema) {
    this.dbUrl = url;
    this.dbUser = user;
    this.dbPassword = password;
    this.liquibaseScript = liquibaseScript;
    this.dbSchema = dbSchema;
  }

  public UpgradeDbLiquibaseJob(String url, String user, String password, String liquibaseScript) {
    this(url, user, password, liquibaseScript, null);
  }

  @Override
  public void run() {
    Database database = null;
    try {
      database = getDatabase();
      if (StringUtils.isNotEmpty(dbSchema)) {
        database.setDefaultSchemaName(dbSchema);
      }
      final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
      new Liquibase(liquibaseScript, resourceAccessor, database).update((String) null);
    } catch (SQLException | LiquibaseException e) {
      throw new DaoException(
          "Upgrading of DB with [" + liquibaseScript + "] is failed: " + e.getMessage(),
          e); // NOSONAR
    } finally {
      if (database != null) {
        try {
          database.close();
        } catch (DatabaseException e) {
          log.error("Could not close DB during CANS DB upgrade: " + e.getMessage(), e);
        }
      }
    }
  }

  private Database getDatabase() throws DatabaseException, SQLException {
    return DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(
            new JdbcConnection(DriverManager.getConnection(dbUrl, dbUser, dbPassword)));
  }
}

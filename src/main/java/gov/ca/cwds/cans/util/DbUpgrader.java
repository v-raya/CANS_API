package gov.ca.cwds.cans.util;

import gov.ca.cwds.cans.CansConfiguration;
import io.dropwizard.db.DataSourceFactory;
import java.sql.Connection;
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

/**
 * @author denys.davydov
 */
@Slf4j
public final class DbUpgrader {

  private static final String LB_SCRIPT_CREATE_SCHEMA = "liquibase/util/cans_schema.xml";
  private static final String LB_SCRIPT_CANS_MASTER = "liquibase/cans_database_master.xml";
  private static final String LB_SCRIPT_DEMO_MASTER = "liquibase/cans_database_demo_master.xml";
  private static final String HIBERNATE_DEFAULT_SCHEMA = "hibernate.default_schema";

  private DbUpgrader() {
  }

  public static void upgradeCansDb(CansConfiguration configuration)  {
    log.info("Upgrading CANS DB...");
    Database database = null;
    try {
      final DataSourceFactory dataSourceFactory = configuration.getCansDataSourceFactory();
      database = getDatabase(dataSourceFactory);
      final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
      new Liquibase(LB_SCRIPT_CREATE_SCHEMA, resourceAccessor, database)
          .update((String) null);
      final String schemaName = dataSourceFactory.getProperties().get(HIBERNATE_DEFAULT_SCHEMA);
      database.setDefaultSchemaName(schemaName);
      new Liquibase(LB_SCRIPT_CANS_MASTER, resourceAccessor, database)
          .update((String) null);
    } catch (SQLException | LiquibaseException e) {
      log.error("Upgrading of CANS DB is failed: " + e.getMessage(), e);
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

  public static void runDmlOnCansDb(CansConfiguration configuration)  {
    log.info("Running dml scripts on CANS DB...");
    Database database = null;
    try {
      final DataSourceFactory dataSourceFactory = configuration.getCansDataSourceFactory();
      database = getDatabase(dataSourceFactory);
      final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
      final String schemaName = dataSourceFactory.getProperties().get(HIBERNATE_DEFAULT_SCHEMA);
      database.setDefaultSchemaName(schemaName);
      new Liquibase(LB_SCRIPT_DEMO_MASTER, resourceAccessor, database)
          .update((String) null);
    } catch (SQLException | LiquibaseException e) {
      log.error("Running dml scripts on CANS DB is failed: " + e.getMessage(), e);
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

  private static Database getDatabase(DataSourceFactory dataSourceFactory)
      throws SQLException, DatabaseException {
      final Connection connection = DriverManager.getConnection(
          dataSourceFactory.getUrl(),
          dataSourceFactory.getUser(),
          dataSourceFactory.getPassword()
      );
      return DatabaseFactory.getInstance()
          .findCorrectDatabaseImplementation(new JdbcConnection(connection));
  }

}

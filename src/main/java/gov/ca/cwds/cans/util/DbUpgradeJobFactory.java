package gov.ca.cwds.cans.util;

import gov.ca.cwds.cans.CansConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * @author CWDS TPT-2 Team
 */
public class DbUpgradeJobFactory {

  private static final String LB_SCRIPT_CREATE_SCHEMA = "liquibase/util/cans_schema.xml";
  private static final String LB_SCRIPT_CANS_MASTER = "liquibase/cans_database_master.xml";
  private static final String LB_SCRIPT_DEMO_MASTER = "liquibase/cans_database_demo_master.xml";

  private static final String HIBERNATE_DEFAULT_SCHEMA = "hibernate.default_schema";

  private final DataSourceFactory dataSourceFactory;
  private final String schemaName;

  private DbUpgradeJobFactory(CansConfiguration configuration) {
    this.dataSourceFactory = configuration.getCansDataSourceFactory();
    this.schemaName = dataSourceFactory.getProperties().get(HIBERNATE_DEFAULT_SCHEMA);
  }

  public static DbUpgradeJobFactory newInstance(CansConfiguration configuration) {
    return new DbUpgradeJobFactory(configuration);
  }

  public DbUpgradeJob getCreateSchemaJob() {
    return new UpgradeDbLiquibaseJob(
        dataSourceFactory.getUrl(),
        dataSourceFactory.getUser(),
        dataSourceFactory.getPassword(),
        LB_SCRIPT_CREATE_SCHEMA);
  }

  public DbUpgradeJob getUpgradeDbStructureJob() {
    return new UpgradeDbLiquibaseJob(
        dataSourceFactory.getUrl(),
        dataSourceFactory.getUser(),
        dataSourceFactory.getPassword(),
        LB_SCRIPT_CANS_MASTER,
        schemaName);
  }

  public DbUpgradeJob getCansDemoDataJobs() {
    return new UpgradeDbLiquibaseJob(
        dataSourceFactory.getUrl(),
        dataSourceFactory.getUser(),
        dataSourceFactory.getPassword(),
        LB_SCRIPT_DEMO_MASTER,
        schemaName);
  }

  public DbUpgradeJob getExternalIdConverterJob() {
    return new ExternalIdConverterJob(dataSourceFactory.getUrl(), dataSourceFactory.getUser(),
        dataSourceFactory.getPassword(), schemaName);
  }
}

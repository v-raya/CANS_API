package gov.ca.cwds.cans;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.inject.Injector;
import com.google.inject.Module;
import gov.ca.cwds.cans.Constants.UnitOfWork;
import gov.ca.cwds.cans.cache.CachingModule;
import gov.ca.cwds.cans.inject.ApplicationModule;
import gov.ca.cwds.cans.inject.DataAccessModule;
import gov.ca.cwds.cans.inject.InjectorHolder;
import gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer;
import gov.ca.cwds.cans.rest.filters.RequestExecutionContextFilter;
import gov.ca.cwds.cans.rest.filters.RequestResponseLoggingFilter;
import gov.ca.cwds.cans.security.ClientCompleteAssessmentAuthorizer;
import gov.ca.cwds.cans.security.ClientCreateAssessmentAuthorizer;
import gov.ca.cwds.cans.security.ClientReadAuthorizer;
import gov.ca.cwds.cans.security.StaffPersonReadAuthorizer;
import gov.ca.cwds.cans.security.assessment.AssessmentOperation;
import gov.ca.cwds.cans.security.assessment.AssessmentUpdateAuthorizer;
import gov.ca.cwds.cans.util.DbUpgradeJobFactory;
import gov.ca.cwds.cans.util.DbUpgrader;
import gov.ca.cwds.cans.util.DbUpgrader.DbUpgraderBuilder;
import gov.ca.cwds.rest.BaseApiApplication;
import gov.ca.cwds.security.module.SecurityModule;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.Arrays;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author denys.davydov */
public class CansApplication extends BaseApiApplication<CansConfiguration> {

  private static final Logger LOG = LoggerFactory.getLogger(CansApplication.class);

  public static void main(String[] args) throws Exception {
    new CansApplication().run(args);
  }

  @Override
  public Module applicationModule(final Bootstrap<CansConfiguration> bootstrap) {
    return new ApplicationModule<CansConfiguration>(bootstrap) {
      @Override
      protected void configure() {
        super.configure();
        install(new DataAccessModule(bootstrap));
        SecurityModule securityModule =
            new SecurityModule(BaseApiApplication::getInjector)
                .addStaticAuthorizer(CansStaticAuthorizer.class)
                .addAuthorizer("client:read", ClientReadAuthorizer.class)
                // TODO: Authorizer.checkInstance rest wrapper must be implemented.
                .addAuthorizer("client:createAssessment", ClientCreateAssessmentAuthorizer.class)
                .addAuthorizer(
                    "client:completeAssessment", ClientCompleteAssessmentAuthorizer.class)
                .addAuthorizer("assessment:write", AssessmentUpdateAuthorizer.class)
                .addAuthorizer("staff:read", StaffPersonReadAuthorizer.class);
        Arrays.stream(AssessmentOperation.values())
            .forEach(
                assessmentOperation ->
                    securityModule.addAuthorizer(
                        assessmentOperation.getPermission(), assessmentOperation.getAuthorizer()));
        install(securityModule);
        install(new CachingModule());
      }
    };
  }

  @Override
  public void runInternal(CansConfiguration configuration, Environment environment) {
    upgradeDbIfNeeded(configuration);

    environment
        .getObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    environment
        .jersey()
        .getResourceConfig()
        .packages(getClass().getPackage().getName())
        .register(DeclarativeLinkingFeature.class);

    runDataSourceHealthChecks(environment);

    Injector injector = guiceBundle.getInjector();

    // Providing access to the guice injector from external classes such as custom validators
    InjectorHolder.INSTANCE.setInjector(injector);

    environment
        .servlets()
        .addFilter(
            "RequestExecutionContextManagingFilter",
            injector.getInstance(RequestExecutionContextFilter.class))
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

    environment
        .servlets()
        .addFilter(
            "AuditAndLoggingFilter", injector.getInstance(RequestResponseLoggingFilter.class))
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
  }

  private void upgradeDbIfNeeded(CansConfiguration configuration) {
    DbUpgraderBuilder builder = DbUpgrader.getBuilder();
    DbUpgradeJobFactory jobFactory = DbUpgradeJobFactory.newInstance(configuration);
    if (isTrue(configuration.getUpgradeCansDbOnStart())) {
      builder.add(jobFactory.getCreateSchemaJob());
      builder.add(jobFactory.getUpgradeDbStructureJob());
      builder.add(jobFactory.getExternalIdConverterJob());
    }
    if (isTrue(configuration.getPopulateDemoDataOnStart())) {
      builder.add(jobFactory.getCansDemoDataJobs());
    }

    builder.build().upgradeDb();
  }

  private void runDataSourceHealthChecks(Environment environment) {
    HealthCheckRegistry healthCheckRegistry = environment.healthChecks();
    doHealthCheck(healthCheckRegistry, UnitOfWork.CANS);
    doHealthCheck(healthCheckRegistry, UnitOfWork.CMS);
    doHealthCheck(healthCheckRegistry, UnitOfWork.CMS_RS);
  }

  private void doHealthCheck(HealthCheckRegistry healthCheckRegistry, String key) {
    HealthCheck.Result result = healthCheckRegistry.runHealthCheck(key);
    if (!result.isHealthy()) {
      LOG.error("Fail - {}: {}", key, result.getMessage());
    }
  }
}

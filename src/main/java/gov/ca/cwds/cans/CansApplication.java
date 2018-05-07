package gov.ca.cwds.cans;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.inject.Injector;
import com.google.inject.Module;
import gov.ca.cwds.cans.inject.ApplicationModule;
import gov.ca.cwds.cans.inject.DataAccessModule;
import gov.ca.cwds.cans.inject.InjectorHolder;
import gov.ca.cwds.cans.rest.filters.RequestExecutionContextFilter;
import gov.ca.cwds.cans.rest.filters.RequestResponseLoggingFilter;
import gov.ca.cwds.rest.BaseApiApplication;
import gov.ca.cwds.security.module.SecurityModule;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * @author denys.davydov
 */

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
        install(new SecurityModule(BaseApiApplication::getInjector));
      }
    };
  }

  @Override
  public void runInternal(CansConfiguration configuration, Environment environment) {
    environment.getObjectMapper()
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

    environment.servlets()
            .addFilter("RequestExecutionContextManagingFilter",
                    injector.getInstance(RequestExecutionContextFilter.class))
            .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

    environment.servlets()
            .addFilter("AuditAndLoggingFilter",
                    injector.getInstance(RequestResponseLoggingFilter.class))
            .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
  }

  private void runDataSourceHealthChecks(Environment environment) {
    HealthCheckRegistry healthCheckRegistry = environment.healthChecks();
    doHealthCheck(healthCheckRegistry, Constants.UnitOfWork.CANS);
  }

  private void doHealthCheck(HealthCheckRegistry healthCheckRegistry, String key) {
    HealthCheck.Result result = healthCheckRegistry.runHealthCheck(key);
    if (!result.isHealthy()) {
      LOG.error("Fail - {}: {}", key, result.getMessage());
    }
  }

}

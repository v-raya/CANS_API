package gov.ca.cwds.cans.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import gov.ca.cwds.cans.CansConfiguration;
import gov.ca.cwds.inject.AuditingModule;
import gov.ca.cwds.rest.WebSecurityConfiguration;
import gov.ca.cwds.security.configuration.SecurityConfiguration;
import io.dropwizard.setup.Bootstrap;

/**
 * Bootstraps and configures the CWDS RESTful CM_UNIT_OF_WORK-API application.
 *
 * @author denys.davydov
 */

public class ApplicationModule<T extends CansConfiguration> extends AbstractModule {

  private Bootstrap<T> bootstrap;

  public ApplicationModule(Bootstrap<T> bootstrap) {
    super();
    this.bootstrap = bootstrap;
  }

  /**
   * Configure and initialize API components, including services, rest, data access objects (DAO),
   * web service filters, and auditing.
   *
   * {@inheritDoc}
   */
  @Override
  protected void configure() {
    install(new AuditingModule());
    install(new MappingModule());
    install(new FiltersModule());
  }

  public Bootstrap<T> getBootstrap() {
    return bootstrap;
  }

  @Provides
  public WebSecurityConfiguration provideWebSecurityConfiguration(T configuration) {
    return configuration.getWebSecurityConfiguration();
  }

  @Provides
  public SecurityConfiguration provideSecurityConfiguration(T configuration) {
    return configuration.getSecurityConfiguration();
  }

}

package gov.ca.cwds.cans.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.cans.CansApplication;
import gov.ca.cwds.cans.rest.filters.RequestExecutionContextFilter;
import gov.ca.cwds.cans.rest.filters.RequestResponseLoggingFilter;
import gov.ca.cwds.rest.filters.WebSecurityFilter;

/**
 * Dependency injection (DI) for Filter classes.
 *
 * <p>Register filters her with Guice and configure them in {@link CansApplication}, method
 * registerFilters.
 *
 * @author CWDS API Team
 */
public class FiltersModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(RequestExecutionContextFilter.class);
    bind(RequestResponseLoggingFilter.class);
    bind(WebSecurityFilter.class);
  }
}

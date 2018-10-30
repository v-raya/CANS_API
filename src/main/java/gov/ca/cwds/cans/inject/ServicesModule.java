package gov.ca.cwds.cans.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.cans.inject.provider.ClientsServiceProvider;
import gov.ca.cwds.cans.inject.provider.PersonServiceProvider;
import gov.ca.cwds.cans.inject.provider.StaffServiceProvider;
import gov.ca.cwds.cans.service.ClientsService;
import gov.ca.cwds.cans.service.PersonService;
import gov.ca.cwds.cans.service.StaffService;

/**
 * Identifies all CANS API business layer (services) classes available for dependency injection by
 * Guice.
 *
 * @author CANS Team
 */
public class ServicesModule extends AbstractModule {

  /** Default constructor */
  public ServicesModule() {
    // Do nothing - Default constructor
  }

  @Override
  protected void configure() {
    bind(StaffService.class).toProvider(StaffServiceProvider.class);
    bind(PersonService.class).toProvider(PersonServiceProvider.class);
    bind(ClientsService.class).toProvider(ClientsServiceProvider.class);
  }
}

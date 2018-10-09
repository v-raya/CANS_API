package gov.ca.cwds.cans.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.cans.service.StaffService;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/** Guice provider for proxy service creating */
public class StaffServiceProvider extends AbstractInjectProvider<StaffService> {

  @Inject
  public StaffServiceProvider(
      Injector injector, UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<StaffService> getServiceClass() {
    return StaffService.class;
  }
}

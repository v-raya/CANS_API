package gov.ca.cwds.cans.inject.provider;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.cans.service.ChildrenService;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/** @author TPT-2 Team */
public class ChildrenServiceProvider extends AbstractInjectProvider<ChildrenService> {
  @Inject
  public ChildrenServiceProvider(
      Injector injector, UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<ChildrenService> getServiceClass() {
    return ChildrenService.class;
  }
}

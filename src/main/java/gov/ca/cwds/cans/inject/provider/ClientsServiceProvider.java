package gov.ca.cwds.cans.inject.provider;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.cans.service.ClientsService;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/** @author TPT-2 Team */
public class ClientsServiceProvider extends AbstractInjectProvider<ClientsService> {
  @Inject
  public ClientsServiceProvider(
      Injector injector, UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<ClientsService> getServiceClass() {
    return ClientsService.class;
  }
}

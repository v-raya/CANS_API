package gov.ca.cwds.cans.inject.provider;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.cans.service.StatisticsService;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/** @author denys.davydov */
public class StatisticsServiceProvider extends AbstractInjectProvider<StatisticsService> {
  @Inject
  public StatisticsServiceProvider(
      Injector injector, UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<StatisticsService> getServiceClass() {
    return StatisticsService.class;
  }
}

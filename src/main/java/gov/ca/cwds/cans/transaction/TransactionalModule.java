package gov.ca.cwds.cans.transaction;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

// TODO move to api-core
public class TransactionalModule extends AbstractModule {

  @Override
  protected void configure() {
    UnitOfWorkInterceptor unitOfWorkInterceptor =
        new UnitOfWorkInterceptor(binder().getProvider(UnitOfWorkAwareProxyFactory.class));
    this.bindInterceptor(
        Matchers.inSubpackage("gov.ca.cwds"),
        Matchers.annotatedWith(UnitOfWork.class),
        unitOfWorkInterceptor);
  }
}

package gov.ca.cwds.cans.inject.provider;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.lang.reflect.ParameterizedType;

public abstract class TransactionalServiceProvider<T> implements Provider<T> {

  @Inject
  private UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory;
  @Inject
  private Injector injector;
  private Class[] parameterTypes;
  private Object[] arguments;

  public TransactionalServiceProvider(Object... arguments) {
    parameterTypes = getClass().getConstructors()[0].getParameterTypes();
    this.arguments = arguments;
  }

  @Override
  public T get() {
    T service = unitOfWorkAwareProxyFactory.create(serviceClass(), parameterTypes, arguments);
    injector.injectMembers(service);
    return service;
  }

  @SuppressWarnings("unchecked")
  private Class<T> serviceClass() {
    return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
        .getActualTypeArguments()[0];
  }
}

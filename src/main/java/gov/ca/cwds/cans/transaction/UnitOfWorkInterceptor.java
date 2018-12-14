package gov.ca.cwds.cans.transaction;

import com.google.inject.Provider;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.hibernate.UnitOfWorkAspect;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.lang.reflect.InvocationTargetException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class UnitOfWorkInterceptor implements MethodInterceptor {

  private Provider<UnitOfWorkAwareProxyFactory> proxyFactory;

  public UnitOfWorkInterceptor(Provider<UnitOfWorkAwareProxyFactory> proxyFactory) {
    this.proxyFactory = proxyFactory;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    final UnitOfWork unitOfWork = invocation.getMethod().getAnnotation(UnitOfWork.class);
    final UnitOfWorkAspect unitOfWorkAspect = proxyFactory.get().newAspect();
    try {
      unitOfWorkAspect.beforeStart(unitOfWork);
      Object result = invocation.proceed();
      unitOfWorkAspect.afterEnd();
      return result;
    } catch (InvocationTargetException e) {
      unitOfWorkAspect.onError();
      throw e.getCause();
    } catch (Exception e) {
      unitOfWorkAspect.onError();
      throw e;
    } finally {
      unitOfWorkAspect.onFinish();
    }
  }
}

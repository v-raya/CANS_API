package gov.ca.cwds.cans.cache;

import com.google.inject.Provider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class CachingInterceptor implements MethodInterceptor {

  private Provider<CacheManager> cacheProvider;

  CachingInterceptor(Provider<CacheManager> cacheProvider) {
    this.cacheProvider = cacheProvider;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Cached cached = invocation.getMethod().getAnnotation(Cached.class);
    KeyGenerator keyGenerator = cached.generator().newInstance();
    CacheKey key = keyGenerator.generate(invocation);
    Cache cache = cacheProvider.get().getCache(getCacheName(cached, invocation.getThis()));
    if (cache.containsKey(key)) {
      return cache.get(key);
    }
    Object value = invocation.proceed();
    cache.put(key, value);
    return value;
  }

  private String getCacheName(Cached cached, Object target) {
    return cached.value().isEmpty() ? target.getClass().getName() : cached.value();
  }

}

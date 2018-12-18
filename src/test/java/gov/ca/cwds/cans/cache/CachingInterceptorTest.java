package gov.ca.cwds.cans.cache;

import com.google.inject.Provider;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class CachingInterceptorTest extends TestService {

  @Test
  @SuppressWarnings("unchecked")
  public void test() throws Throwable {

    CacheManager cacheManager = new CacheManager();
    Provider<CacheManager> provider = Mockito.mock(Provider.class);
    Mockito.when(provider.get()).thenReturn(cacheManager);
    MethodInvocation methodInvocation = mockInvocation();
    CachingInterceptor cachingInterceptor = new CachingInterceptor(provider);
    Object result = cachingInterceptor.invoke(methodInvocation);
    Object result2 = cachingInterceptor.invoke(methodInvocation);
    Assert.assertEquals(RESULT, result);

    Cache cache = cacheManager.getCache(CachingInterceptorTest.class.getName());
    Assert.assertEquals(1, cache.size());
    Assert.assertEquals(RESULT, cache.get(expectedCacheKey()));
    Mockito.verify(methodInvocation, Mockito.times(1)).proceed();
    Assert.assertEquals(result, result2);
  }
}

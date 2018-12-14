package gov.ca.cwds.cans.cache;

import java.util.Arrays;
import org.aopalliance.intercept.MethodInvocation;
import org.mockito.Mockito;

public class TestService {
  final String METHOD_NAME = "testMethod";
  final Integer ARG0 = 2;
  final String ARG1 = "arg1";
  final String RESULT = "result";

  @Cached
  public String testMethod(Integer arg0, String arg1) {
    return RESULT;
  }

  protected MethodInvocation mockInvocation()
      throws Throwable {
    MethodInvocation invocation = Mockito.mock(MethodInvocation.class);
    Mockito.when(invocation.getMethod()).thenReturn(
        CachingInterceptorTest.class.getMethod(METHOD_NAME, Integer.class, String.class));
    Mockito.when(invocation.getArguments()).thenReturn(Arrays.asList(ARG0, ARG1).toArray());
    Mockito.when(invocation.getThis()).thenReturn(this);
    Mockito.when(invocation.proceed()).thenReturn(this.testMethod(ARG0, ARG1));
    return invocation;
  }

  protected CacheKey expectedCacheKey() {
   return new CacheKey(METHOD_NAME, ARG0, ARG1);
  }
}

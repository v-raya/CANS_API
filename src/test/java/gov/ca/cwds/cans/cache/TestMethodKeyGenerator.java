package gov.ca.cwds.cans.cache;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;

public class TestMethodKeyGenerator extends TestService {

  @Test
  public void testKeyGeneration() throws Throwable {
    MethodKeyGenerator keyGenerator = new MethodKeyGenerator();
    MethodInvocation invocation = mockInvocation();
    CacheKey key = keyGenerator.generate(invocation);
    Assert.assertEquals(expectedCacheKey(), key);
  }
}

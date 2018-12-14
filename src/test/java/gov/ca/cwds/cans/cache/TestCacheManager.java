package gov.ca.cwds.cans.cache;

import org.junit.Assert;
import org.junit.Test;

public class TestCacheManager {
  @Test
  public void testCacheCreation() {
    CacheManager cacheManager = new CacheManager();
    Cache c1 = cacheManager.getCache("c1");
    Cache c2 = cacheManager.getCache("c1");
    Cache c3 = cacheManager.getCache("c3");

    c2.put(new CacheKey(), 1);

    Assert.assertEquals(c1, c2);
    Assert.assertNotEquals(c1, c3);
    Assert.assertNotEquals(c2, c3);
  }
}

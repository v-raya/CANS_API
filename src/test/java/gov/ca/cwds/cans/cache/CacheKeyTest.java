package gov.ca.cwds.cans.cache;

import gov.ca.cwds.cans.domain.entity.Person;
import org.junit.Assert;
import org.junit.Test;

public class CacheKeyTest {
  @Test
  public void checkEqualsAndHashCode() {
    Person person1 = new Person();
    person1.setId(1L);
    CacheKey key1 = new CacheKey("method", person1);
    CacheKey key2 = new CacheKey("method", person1);
    CacheKey key3 = new CacheKey("method", person1, "arg1");

    // equals
    Assert.assertEquals(key1, key2);
    Assert.assertNotEquals(key1, key3);
    Assert.assertNotEquals(key2, key3);

    // hashCode
    Assert.assertEquals(key1.hashCode(), key2.hashCode());
    Assert.assertNotEquals(key1.hashCode(), key3.hashCode());
    Assert.assertNotEquals(key2.hashCode(), key3.hashCode());
  }
}

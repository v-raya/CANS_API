package gov.ca.cwds.cans.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

  private Map<String, Cache> caches = new HashMap<>();

  public Cache getCache(String cache) {
    if (!caches.containsKey(cache)) {
      caches.put(cache, new Cache());
    }
    return caches.get(cache);
  }
}

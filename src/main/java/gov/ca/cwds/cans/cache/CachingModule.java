package gov.ca.cwds.cans.cache;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.RequestScoped;

//TODO move to api-core
public class CachingModule extends AbstractModule {

  @Override
  protected void configure() {
    Provider<CacheManager> cacheProvider = binder().getProvider(Key.get(CacheManager.class));
    CachingInterceptor cachingInterceptor = new CachingInterceptor(cacheProvider);
    this.bindInterceptor(
        Matchers.inSubpackage("gov.ca.cwds"),
        Matchers.annotatedWith(Cached.class),
        cachingInterceptor);
  }

  @Provides
  @RequestScoped
  public CacheManager cacheManager() {
    return new CacheManager();
  }
}

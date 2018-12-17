package gov.ca.cwds.cans.security;

import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import gov.ca.cwds.cans.domain.mapper.AssessmentMapper;
import gov.ca.cwds.cans.domain.mapper.ClientMapper;
import gov.ca.cwds.cans.util.MethodNameMatcher;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import gov.ca.cwds.security.authorizer.StaticAuthorizer;
import gov.ca.cwds.security.module.InjectorProvider;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.mapstruct.factory.Mappers;

// TODO move to api-core
public class SecurityModule extends gov.ca.cwds.security.module.SecurityModule {

  private Map<Class<?>, Set<String>> permissionsRegistry = new HashMap<>();

  public SecurityModule(InjectorProvider injector) {
    super(injector);
  }

  private static Class getSecuredInstanceType(Class<? extends BaseAuthorizer> authorizer) {
    return ((Class) extractParameterizedType(authorizer).getActualTypeArguments()[0]);
  }

  private static ParameterizedType extractParameterizedType(Class<? extends BaseAuthorizer> clazz) {
    Type type = clazz.getGenericSuperclass();
    while (!(type instanceof ParameterizedType)) {
      type = ((Class) type).getGenericSuperclass();
    }
    return (ParameterizedType) type;
  }

  public SecurityModule addAuthorizer(String permission, Class<? extends BaseAuthorizer> clazz) {
    Class securedType = getSecuredInstanceType(clazz);
    registerPermission(permission, securedType);
    return (SecurityModule) super.addAuthorizer(permission, clazz);
  }

  public SecurityModule addStaticAuthorizer(Class<? extends StaticAuthorizer> clazz) {
    return (SecurityModule) super.addStaticAuthorizer(clazz);
  }

  @Provides
  public PermissionService permissionRegistryService() {
    return new PermissionService(permissionsRegistry);
  }

  protected void configure() {
    Map<Class, Set<String>> cache = new HashMap<>();
    permissionsRegistry.forEach((k, v) -> cache.put(k, Collections.unmodifiableSet(v)));
    permissionsRegistry = Collections.unmodifiableMap(permissionsRegistry);
    bindPermissionInterceptors(AssessmentMapper.class, ClientMapper.class);
    super.configure();
  }

  private void bindPermissionInterceptors(Class<?>... mappers) {
    Provider<PermissionService> permissionServiceProvider =
        binder().getProvider(PermissionService.class);
    PermissionInterceptor permissionInterceptor =
        new PermissionInterceptor(permissionServiceProvider);
    Matcher<Method> toDtoMatcher = new MethodNameMatcher("toDto");
    for (Class<?> mapper : mappers) {
      bindInterceptor(
          Matchers.subclassesOf(Mappers.getMapper(mapper).getClass()),
          toDtoMatcher,
          permissionInterceptor);
    }
  }

  private void registerPermission(String permission, Class securedType) {
    if (!permissionsRegistry.containsKey(securedType)) {
      permissionsRegistry.put(securedType, new HashSet<>());
    }
    permissionsRegistry.get(securedType).add(permission);
  }
}

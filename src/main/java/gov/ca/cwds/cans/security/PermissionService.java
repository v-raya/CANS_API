package gov.ca.cwds.cans.security;

import com.google.inject.Key;
import com.google.inject.name.Names;
import gov.ca.cwds.rest.api.ApiException;
import gov.ca.cwds.security.authorizer.Authorizer;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// TODO move to api-core
public class PermissionService {

  private Map<Class<?>, Set<String>> registry;

  PermissionService(Map<Class<?>, Set<String>> registry) {
    this.registry = registry;
  }

  public Set<String> getAllowedOperations(Object secured) {
    Set<String> permissions = findPermissions(secured);
    Set<String> result =
        permissions
            .stream()
            .filter(permission -> getPermissionHandler(permission).check(secured))
            .map(this::extractOperation)
            .collect(Collectors.toSet());
    return Collections.unmodifiableSet(result);
  }

  private Set<String> findPermissions(Object secured) {
    Set<String> permissions = registry.get(secured.getClass());
    if (permissions == null) {
      Optional<Class<?>> securedType =
          registry.keySet().stream().filter(c -> c.isAssignableFrom(secured.getClass())).findAny();
      if (securedType.isPresent()) {
        permissions = registry.get(securedType.get());
      } else {
        throw new ApiException("Object of type: " + secured.getClass() + " is not secured!");
      }
    }
    return permissions;
  }

  private Authorizer getPermissionHandler(String name) {
    return SecurityModule.injector().getInstance(Key.get(Authorizer.class, Names.named(name)));
  }

  private String extractOperation(String permission) {
    return permission.split(":")[1];
  }
}

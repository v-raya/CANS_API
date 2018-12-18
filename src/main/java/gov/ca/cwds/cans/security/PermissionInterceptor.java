package gov.ca.cwds.cans.security;

import com.google.inject.Provider;
import gov.ca.cwds.cans.domain.dto.Dto;
import java.util.Set;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class PermissionInterceptor implements MethodInterceptor {

  private static final String OPERATIONS_METADATA_KEY = "allowed_operations";

  private Provider<PermissionService> permissionServiceProvider;

  public PermissionInterceptor(Provider<PermissionService> permissionServiceProvider) {
    this.permissionServiceProvider = permissionServiceProvider;
  }

  @Override
  public Object invoke(MethodInvocation toDto) throws Throwable {
    Dto dto = (Dto) toDto.proceed();
    Set<String> allowedOperations =
        permissionServiceProvider.get().getAllowedOperations(toDto.getArguments()[0]);
    dto.addMetadata(OPERATIONS_METADATA_KEY, allowedOperations);
    return dto;
  }
}

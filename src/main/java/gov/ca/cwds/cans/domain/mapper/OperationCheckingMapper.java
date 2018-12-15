package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.entity.Persistent;
import gov.ca.cwds.cans.security.PermissionService;
import java.util.Collection;
import java.util.Set;

public class OperationCheckingMapper<E extends Persistent, D extends Dto, M extends AMapper<E, D>>
    implements AMapper<E, D> {

  private static final String OPERATIONS_METADATA_KEY = "allowed_operations";
  private M delegate;
  private PermissionService permissionService;

  public OperationCheckingMapper(M delegate, PermissionService permissionService) {
    this.delegate = delegate;
    this.permissionService = permissionService;
  }

  public M getDelegate() {
    return delegate;
  }

  @Override
  public D toDto(E entity) {
    D result = delegate.toDto(entity);
    populateOperations(entity, result);
    return result;
  }

  private void populateOperations(E entity, D result) {
    Set<String> operations = permissionService.getAllowedOperations(entity);
    result.addMetadata(OPERATIONS_METADATA_KEY, operations);
  }

  @Override
  public Collection<D> toDtos(Collection<E> entity) {
    return delegate.toDtos(entity);
  }

  @Override
  public E fromDto(D dto) {
    return delegate.fromDto(dto);
  }

  @Override
  public Collection<E> fromDtos(Collection<D> dtos) {
    return fromDtos(dtos);
  }
}

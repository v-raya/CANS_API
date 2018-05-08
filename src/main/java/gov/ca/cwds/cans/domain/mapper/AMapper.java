package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.data.persistence.PersistentObject;
import java.util.Collection;

/**
 * @author denys.davydov
 */
public interface AMapper<E extends PersistentObject, D extends Dto> {

  D toDto(E entity);
  Collection<D> toDtos(Collection<E> entity);

  E fromDto(D dto);
  Collection<E> fromDtos(Collection<D> dtos);

}

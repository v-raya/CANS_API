package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.entity.Persistent;
import java.util.Collection;

/** @author denys.davydov */
public interface AMapper<E extends Persistent, D extends Dto> {

  D toDto(E entity);

  Collection<D> toDtos(Collection<E> entity);

  E fromDto(D dto);

  Collection<E> fromDtos(Collection<D> dtos);
}

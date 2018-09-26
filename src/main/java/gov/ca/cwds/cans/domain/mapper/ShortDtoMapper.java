package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.entity.Persistent;
import java.util.Collection;

/** @author denys.davydov */
public interface ShortDtoMapper<E extends Persistent, D extends Dto> {
  D toShortDto(E entity);
  Collection<D> toShortDtos(Collection<E> entity);
}

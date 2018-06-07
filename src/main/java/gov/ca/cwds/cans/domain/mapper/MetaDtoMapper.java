package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.entity.Persistent;
import java.util.Collection;

/** @author denys.davydov */
public interface MetaDtoMapper<E extends Persistent, D extends Dto> {
  D toMetaDto(E entity);
  Collection<D> toMetaDtos(Collection<E> entity);
}

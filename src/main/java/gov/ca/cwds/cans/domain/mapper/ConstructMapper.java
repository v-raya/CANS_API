package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.ConstructDto;
import gov.ca.cwds.cans.domain.entity.Construct;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper(uses = CountyMapper.class)
public interface ConstructMapper {
  ConstructDto toDto(Construct entity);

  Construct fromDto(ConstructDto entity);
}

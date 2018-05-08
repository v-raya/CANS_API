package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import java.util.Collection;
import org.mapstruct.Mapper;

/**
 * @author denys.davydov
 */
@Mapper
public interface CountyMapper {

  CountyDto toDto(County entity);
  Collection<CountyDto> toDtos(Collection<County> entity);

  County fromDto(CountyDto dto);
  Collection<County> fromDtos(Collection<CountyDto> dto);

}

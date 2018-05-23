package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.entity.County;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface CountyMapper extends AMapper<County, CountyDto> {}

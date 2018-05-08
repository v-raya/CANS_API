package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.CftDto;
import gov.ca.cwds.cans.domain.entity.Cft;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper(uses = PersonMapper.class)
public interface CftMapper extends AMapper<Cft, CftDto> {}

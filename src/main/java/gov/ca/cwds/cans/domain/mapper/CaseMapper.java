package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.CaseDto;
import gov.ca.cwds.cans.domain.entity.Case;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper(uses = PersonMapper.class)
public interface CaseMapper extends AMapper<Case, CaseDto> {}

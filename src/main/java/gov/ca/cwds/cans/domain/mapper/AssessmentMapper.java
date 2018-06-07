package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper(uses = {PersonMapper.class})
public interface AssessmentMapper
    extends AMapper<Assessment, AssessmentDto>, MetaDtoMapper<Assessment, AssessmentMetaDto> {}

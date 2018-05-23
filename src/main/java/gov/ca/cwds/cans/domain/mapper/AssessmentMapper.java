package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.AssessmentDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface AssessmentMapper extends AMapper<Assessment, AssessmentDto> {}

package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.assessment.AbstractAssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/** @author denys.davydov */
@Mapper(uses = {PersonMapper.class, ClientMapper.class, CountyMapper.class, CaseMapper.class})
public interface AssessmentMapper
    extends AMapper<Assessment, AssessmentDto>, ShortDtoMapper<Assessment, AssessmentMetaDto> {

  @AfterMapping
  default void afterMapping(@MappingTarget AbstractAssessmentDto dto) {
    final String serviceSourceId = dto.getServiceSourceId();
    if (serviceSourceId != null) {
      dto.setServiceSourceUiId(CmsKeyIdGenerator.getUIIdentifierFromKey(serviceSourceId));
    }
  }
}

package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.assessment.SearchAssessmentRequest;
import gov.ca.cwds.cans.domain.search.SearchAssessmentPo;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface SearchAssessmentMapper
    extends SearchMapper<SearchAssessmentPo, SearchAssessmentRequest> {}

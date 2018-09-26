package gov.ca.cwds.cans.domain.mapper.search;

import gov.ca.cwds.cans.domain.dto.assessment.SearchAssessmentRequest;
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface SearchAssessmentRequestMapper
    extends SearchRequestMapper<SearchAssessmentParameters, SearchAssessmentRequest> {}

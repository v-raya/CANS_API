package gov.ca.cwds.cans.domain.mapper.search;

import gov.ca.cwds.cans.domain.dto.person.SearchPersonRequest;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface SearchPersonRequestMapper
    extends SearchRequestMapper<SearchPersonParameters, SearchPersonRequest> {}

package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.person.SearchPersonRequest;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface SearchPersonMapper extends SearchMapper<SearchPersonParameters, SearchPersonRequest> {}

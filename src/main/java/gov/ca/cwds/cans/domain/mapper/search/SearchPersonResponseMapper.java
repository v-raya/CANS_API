package gov.ca.cwds.cans.domain.mapper.search;

import gov.ca.cwds.cans.domain.dto.person.SearchPersonResponse;
import gov.ca.cwds.cans.domain.mapper.PersonShortMapper;
import gov.ca.cwds.cans.domain.search.SearchPersonResult;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper(uses = PersonShortMapper.class)
public interface SearchPersonResponseMapper
    extends SearchResponseMapper<SearchPersonResult, SearchPersonResponse> {}

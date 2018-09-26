package gov.ca.cwds.cans.domain.mapper.search;

import gov.ca.cwds.cans.domain.dto.SearchResponse;
import gov.ca.cwds.cans.domain.search.SearchResult;

/** @author denys.davydov */
public interface SearchResponseMapper<E extends SearchResult, D extends SearchResponse> {
  D toDto(E entity);
}

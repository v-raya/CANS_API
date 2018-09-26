package gov.ca.cwds.cans.domain.mapper.search;

import gov.ca.cwds.cans.domain.dto.SearchRequest;
import gov.ca.cwds.cans.domain.search.SearchParameters;

/** @author denys.davydov */
public interface SearchRequestMapper<E extends SearchParameters, D extends SearchRequest> {
  D toSearchRequest(E entity);
  E fromSearchRequest(D dto);
}

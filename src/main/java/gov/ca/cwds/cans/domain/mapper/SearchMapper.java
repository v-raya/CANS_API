package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.SearchRequest;
import gov.ca.cwds.cans.domain.search.SearchParameters;

/** @author denys.davydov */
public interface SearchMapper<P extends SearchParameters, R extends SearchRequest> {
  R toSearchRequest(P entity);

  P fromSearchRequest(R dto);
}

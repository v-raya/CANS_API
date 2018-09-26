package gov.ca.cwds.cans.domain.dto.person;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.dto.SearchResponse;

/** @author denys.davydov */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SearchPersonResponse extends SearchResponse<PersonShortDto> {
}

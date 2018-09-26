package gov.ca.cwds.cans.domain.search;

import gov.ca.cwds.cans.domain.entity.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SearchPersonResult extends SearchResult<Person> {}

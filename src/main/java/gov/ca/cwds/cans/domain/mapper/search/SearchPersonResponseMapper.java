package gov.ca.cwds.cans.domain.mapper.search;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.person.SearchPersonResponse;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.mapper.PersonShortMapper;
import gov.ca.cwds.cans.domain.search.SearchPersonResult;
import java.util.Collection;
import org.apache.commons.collections4.CollectionUtils;

/**
 * The mapper is written without using mapstruct to @Inject {@link PersonShortMapper} which
 * lifecycle should be managed by IoC container to support authorization. The current mapstruct
 * version does not support injection of Guice components.
 *
 * @author denys.davydov
 */
public class SearchPersonResponseMapper
    implements SearchResponseMapper<SearchPersonResult, SearchPersonResponse> {

  private final PersonShortMapper personShortMapper;

  @Inject
  public SearchPersonResponseMapper(PersonShortMapper personShortMapper) {
    this.personShortMapper = personShortMapper;
  }

  @Override
  public SearchPersonResponse toDto(SearchPersonResult entity) {
    final SearchPersonResponse response = new SearchPersonResponse();
    final Collection<Person> records = entity.getRecords();
    if (CollectionUtils.isNotEmpty(records)) {
      response.setRecords(personShortMapper.toDtos(records));
    }
    response.setTotalRecords(entity.getTotalRecords());
    return response;
  }
}

package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;

import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import gov.ca.cwds.cans.domain.search.SearchPersonResult;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.security.annotations.Authorize;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Set;

/** @author denys.davydov */
public class PersonService extends AbstractCrudService<Person> {

  public PersonService(PersonDao dao) {
    super(dao);
  }

  public SearchPersonResult search(final SearchPersonParameters searchPersonParameters) {
    final PerryAccount perryAccount = PrincipalUtils.getPrincipal();
    searchPersonParameters.setUsersCountyExternalId(perryAccount.getCountyCwsCode());
    return ((PersonDao) dao).search(searchPersonParameters);
  }

  public Person findByExternalId(final String externalId) {
    return ((PersonDao) dao).findByExternalId(externalId);
  }

  @Override
  public Person create(final Person person) {
    Require.requireNotNullAndNotEmpty(person);
    return super.create(person);
  }

  @UnitOfWork(CANS)
  public List<StaffClientDto> findStatusesByExternalIds(Set<String> externalIds) {
    return ((PersonDao) dao).findStatusesByExternalIds(externalIds);
  }

  @Override
  public Person update(@Authorize("person:write:person.id") final Person person) {
    Require.requireNotNullAndNotEmpty(person);
    return super.update(person);
  }
}

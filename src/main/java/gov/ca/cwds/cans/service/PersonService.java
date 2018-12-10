package gov.ca.cwds.cans.service;

import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.util.Require;
import java.util.List;
import java.util.Set;

/** @author denys.davydov */
public class PersonService extends AbstractCrudService<Person> {

  @SuppressWarnings("unchecked")
  public PersonService(PersonDao dao) {
    super(dao);
  }

  public Person findByExternalId(final String externalId) {
    return ((PersonDao) dao).findByExternalId(externalId);
  }

  @Override
  public Person create(final Person person) {
    Require.requireNotNullAndNotEmpty(person);
    return super.create(person);
  }

  public List<StaffClientDto> findStatusesByExternalIds(Set<String> externalIds) {
    return ((PersonDao) dao).findStatusesByExternalIds(externalIds);
  }

  @Override
  public Person update(final Person person) {
    Require.requireNotNullAndNotEmpty(person);
    return super.update(person);
  }
}

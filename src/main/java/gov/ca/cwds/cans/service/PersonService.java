package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.Person;

/** @author denys.davydov */
public class PersonService extends AbstractCrudService<Person> {

  @Inject
  public PersonService(PersonDao dao) {
    super(dao);
  }
}

package gov.ca.cwds.cans.inject.provider;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.service.PersonService;

public class PersonServiceProvider extends TransactionalServiceProvider<PersonService> {

  @Inject
  public PersonServiceProvider(PersonDao personDao) {
    super(personDao);
  }
}

package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.drools.DroolsConfiguration;

public class PersonReadAuthorizer extends DroolsAuthorizer<Person, Long> {

  private static final String CONFIGURATION_NAME = "authorization-rules";
  private static final String AGENDA_GROUP_NAME = "person-read-authorization-rules";
  @Inject
  private PersonDao personDao;

  public PersonReadAuthorizer() {
    super(new DroolsConfiguration<>(
        CONFIGURATION_NAME,
        AGENDA_GROUP_NAME,
        CONFIGURATION_NAME));
  }

  public PersonReadAuthorizer(DroolsConfiguration<Person> configuration) {
    super(configuration);
  }

  @Override
  protected boolean checkId(Long id) {
    Person person = personDao.find(id);
    return checkInstance(person);
  }

  @Override
  protected Long stringToId(String id) {
    return Long.valueOf(id);
  }
}

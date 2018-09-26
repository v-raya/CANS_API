package gov.ca.cwds.cans.service;

import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import javax.inject.Inject;

/** @author denys.davydov */
public class PerryService {

  private final PersonDao personDao;

  @Inject
  public PerryService(PersonDao personDao) {
    this.personDao = personDao;
  }

  public Person getOrPersistAndGetCurrentUser() {
    final PerryAccount perryAccount = PrincipalUtils.getPrincipal();
    final Person user = personDao.findByExternalId(perryAccount.getUser());
    if (user != null) {
      return user;
    }
    final Person newUser = buildNewUser(perryAccount);
    return personDao.create(newUser);
  }

  private Person buildNewUser(PerryAccount perryAccount) {
    return new Person()
        .setExternalId(perryAccount.getUser())
        .setFirstName(perryAccount.getFirstName())
        .setLastName(perryAccount.getLastName())
        .setPersonRole(PersonRole.USER);
  }
}

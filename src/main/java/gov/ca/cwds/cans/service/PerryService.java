package gov.ca.cwds.cans.service;

import gov.ca.cwds.cans.dao.CountyDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;

import javax.inject.Inject;
import java.util.Collection;

/** @author denys.davydov */
public class PerryService {

  private final PersonDao personDao;
  private final CountyDao countyDao;

  @Inject
  public PerryService(PersonDao personDao, CountyDao countyDao)
  {
    this.personDao = personDao;
    this.countyDao = countyDao;
  }

  public Person getOrPersistAndGetCurrentUser() {
    final PerryAccount perryAccount = PrincipalUtils.getPrincipal();
    final String userUniqueId = perryAccount.getUser();
    final Collection<Person> users = findUserById(userUniqueId, perryAccount.getCountyCwsCode());
    if (!users.isEmpty()) {
      return users.iterator().next();
    }
    final Person newUser = buildNewUser(perryAccount, userUniqueId);
    return personDao.create(newUser);
  }

  private Person buildNewUser(PerryAccount perryAccount, String userUniqueId) {
    return new Person()
        .setExternalId(userUniqueId)
        .setFirstName(perryAccount.getFirstName())
        .setLastName(perryAccount.getLastName())
        .setPersonRole(PersonRole.USER);
  }

  private Collection<Person> findUserById(String userUniqueId, String countyId) {
    final SearchPersonPo searchPo =
        new SearchPersonPo().setExternalId(userUniqueId).setPersonRole(PersonRole.USER);
    return personDao.search(searchPo, countyId);
  }
}

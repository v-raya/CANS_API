package gov.ca.cwds.cans.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(gov.ca.cwds.security.utils.PrincipalUtils.class)
public class PerryServiceTest {

  @Test
  public void getOrPersistAndGetCurrentUser_setsAttributesOnUser() throws Exception {
    PersonDao personDao = mock(PersonDao.class);
    PerryService perryService = new PerryService(personDao);
    PerryAccount perryAccount = new PerryAccount();
    perryAccount.setUser("999");
    perryAccount.setFirstName("Slick");
    perryAccount.setLastName("Rick");
    perryAccount.setCountyCwsCode("1126");

    Person expectedPerson = new Person();
    expectedPerson.setExternalId("999");
    expectedPerson.setFirstName("Slick");
    expectedPerson.setLastName("Rick");
    expectedPerson.setPersonRole(PersonRole.USER);

    mockStatic(PrincipalUtils.class);
    when(PrincipalUtils.getPrincipal()).thenReturn(perryAccount);

    perryService.getOrPersistAndGetCurrentUser();
    verify(personDao).create(expectedPerson);
  }
}

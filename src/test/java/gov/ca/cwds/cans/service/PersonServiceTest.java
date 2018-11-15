package gov.ca.cwds.cans.service;

import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(gov.ca.cwds.security.utils.PrincipalUtils.class)
public class PersonServiceTest {

  @Test
  public void search_returnsPeople() {
    PersonDao personDao = mock(PersonDao.class);
    mockStatic(PrincipalUtils.class);
    PerryAccount perryAccount = new PerryAccount();
    perryAccount.setFirstName("Slick");
    perryAccount.setLastName("Rick");
    perryAccount.setCountyCwsCode("1126");
    when(PrincipalUtils.getPrincipal()).thenReturn(perryAccount);

    PersonService personService = new PersonService(personDao);
    SearchPersonParameters searchPersonParameters =
        new SearchPersonParameters().setUsersCountyExternalId("1126");
    personService.search(searchPersonParameters);
    verify(personDao).search(searchPersonParameters);
  }
}

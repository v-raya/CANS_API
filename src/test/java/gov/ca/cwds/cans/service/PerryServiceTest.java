package gov.ca.cwds.cans.service;

import gov.ca.cwds.cans.dao.CountyDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(gov.ca.cwds.security.utils.PrincipalUtils.class)
public class PerryServiceTest {

  @Test
  public void testGetOrPersistAndGetCurrentUserSetsCounty() throws Exception {
    PersonDao personDao = mock(PersonDao.class);
    CountyDao countyDao = mock(CountyDao.class);
    PerryService perryService = new PerryService(personDao, countyDao);
    PerryAccount perryAccount = new PerryAccount();
    perryAccount.setFirstName("Slick");
    perryAccount.setLastName("Rick");
    perryAccount.setCountyCwsCode("1126");

    Person expectedPersonWithCounty = new Person();
    expectedPersonWithCounty.setFirstName("Slick");
    expectedPersonWithCounty.setLastName("Rick");
    expectedPersonWithCounty.setPersonRole(PersonRole.USER);
    County county = new County();
    county.setId(42L);
    expectedPersonWithCounty.setCounty(county);

    mockStatic(PrincipalUtils.class);
    when(PrincipalUtils.getPrincipal()).thenReturn(perryAccount);
    when(countyDao.findByExternalId("1126")).thenReturn(county);

    perryService.getOrPersistAndGetCurrentUser();
    verify(personDao).create(expectedPersonWithCounty);
  }
}

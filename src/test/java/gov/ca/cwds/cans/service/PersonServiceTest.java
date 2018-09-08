package gov.ca.cwds.cans.service;

import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.dao.CaseDao;
import gov.ca.cwds.cans.dao.CountyDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(gov.ca.cwds.security.utils.PrincipalUtils.class)
public class PersonServiceTest {

  @Test
  public void testSearchReturnsPeople() throws Exception {
    PersonDao personDao = mock(PersonDao.class);
    CountyDao countyDao = mock(CountyDao.class);
    CaseDao caseDao = mock(CaseDao.class);
    AssessmentDao assessmentDao = mock(AssessmentDao.class);
    PerryService perryService = mock(PerryService.class);
    mockStatic(PrincipalUtils.class);
    PerryAccount perryAccount = new PerryAccount();
    perryAccount.setFirstName("Slick");
    perryAccount.setLastName("Rick");
    perryAccount.setCountyCwsCode("1126");
    when(PrincipalUtils.getPrincipal()).thenReturn(perryAccount);
    County county = new County();
    county.setId(42L);
    when(countyDao.findByExternalId("1126")).thenReturn(county);

    PersonService personService = new PersonService(personDao, countyDao, caseDao, assessmentDao, perryService);
    SearchPersonPo searchPersonPo = new SearchPersonPo();
    personService.search(searchPersonPo);
    verify(personDao).search(searchPersonPo, "42");
  }
}

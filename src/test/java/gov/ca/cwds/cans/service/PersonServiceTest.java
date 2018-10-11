package gov.ca.cwds.cans.service;

import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.dao.CaseDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import gov.ca.cwds.cans.util.NullOrEmptyException;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(gov.ca.cwds.security.utils.PrincipalUtils.class)
public class PersonServiceTest {
  PersonDao personDao;
  CaseDao caseDao;
  AssessmentDao assessmentDao;
  PersonService personService;
  SearchPersonParameters searchPersonParameters;
  PerryService perryService;

  @Before
  //Move common setup into a before block. Makes tests simpler
  public void setup() {
    personDao = mock(PersonDao.class);
    caseDao = mock(CaseDao.class);
    assessmentDao = mock(AssessmentDao.class);
    searchPersonParameters =
        new SearchPersonParameters();

    //not responsibility to service, should be removed
    perryService = perryService();
    personService = new PersonService(personDao, caseDao, assessmentDao, perryService) ;
  }


  //PerryService arguably shouldn't be used here. The create should take the county code rather than a service.
  private PerryService perryService() {
    PerryService perryService = mock(PerryService.class);
    mockStatic(PrincipalUtils.class);
    PerryAccount perryAccount = new PerryAccount();
    perryAccount.setFirstName("Slick");
    perryAccount.setLastName("Rick");
    perryAccount.setCountyCwsCode("1126");
    when(PrincipalUtils.getPrincipal()).thenReturn(perryAccount);
    return perryService;
  }

  @Test
  public void searchShouldCallPersonDao() throws Exception {
    searchPersonParameters.setUsersCountyExternalId("1126");
    personService.search(searchPersonParameters);
    verify(personDao).search(searchPersonParameters);
  }

  @Test(expected = NullPointerException.class)
  public void searchShouldThrowNullPointerExceptionWhenParamsAreNull() {
    personService.search(searchPersonParameters);
  }

  //Test should be removed if perry is removed.
  @Test(expected = NullPointerException.class)
  public void searchShouldThrowNullPointerExceptionPerryReturnsNull() {
    when(PrincipalUtils.getPrincipal()).thenReturn(searchPersonParameters);
    personService.search(null);
  }

  @Test
  public void findByExternalIdShouldCallPersonDao() {
    personService.findByExternalId("123");
    verify(personDao).findByExternalId("123");
  }

  @Test
  public void findByExternalIdShouldDoSomethingUndefinedWhenIdIsNull() {
    personService.findByExternalId(null);
    //test for something: exception or some return code? may not be neccesary
  }

  @Test(expected = NullOrEmptyException.class)
  public void createShouldThrowExceptionWhenCreatingAnEmptyPerson() {
    personService.create(null);
  }

  @Test
  public void createShouldCreatePerson() {
    Person person = new Person();
    //some elaborate setup for complex logic
    //Can this code be pushed into different class? Probably
    personService.create(person);
    //some validation. probably several tests.

  }

  @Test
  public void updateShouldUpdatePerson() {
    Person person = new Person();
    //some elaborate setup for complex logic
    //Can this code be pushed into different class? Probably
    personService.update(person);
    //some validation. probably several tests.

  }
}

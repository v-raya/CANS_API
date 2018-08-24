package gov.ca.cwds.cans.service;

import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.dao.CaseDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import org.junit.Test;

import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.verify;

public class PersonServiceTest {

    @Test
    public void testSearchReturnsPeople() throws Exception {
        PersonDao personDao = mock(PersonDao.class);
        CaseDao caseDao = mock(CaseDao.class);
        AssessmentDao assessmentDao = mock(AssessmentDao.class);
        PerryService perryService = mock(PerryService.class);

        PersonService personService = new PersonService(personDao, caseDao, assessmentDao, perryService);
        SearchPersonPo searchPersonPo = new SearchPersonPo();
        personService.search(searchPersonPo);
        verify(personDao).search(searchPersonPo);
    }
}

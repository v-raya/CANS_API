package gov.ca.cwds.cans.inject.provider;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.dao.CaseDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.service.PerryService;
import gov.ca.cwds.cans.service.PersonService;

public class PersonServiceProvider extends TransactionalServiceProvider<PersonService> {

  @Inject
  public PersonServiceProvider(
      PersonDao personDao,
      CaseDao caseDao,
      AssessmentDao assessmentDao,
      PerryService perryService) {
    super(personDao, caseDao, assessmentDao, perryService);
  }
}

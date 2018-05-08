package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.dao.CftDao;
import gov.ca.cwds.cans.dao.ConstructDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.dto.assessment.StartAssessmentRequest;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Cft;
import gov.ca.cwds.cans.domain.entity.Construct;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.util.Require;

/** @author denys.davydov */
public class AssessmentService extends AbstractCrudService<Assessment> {

  private final ConstructDao constructDao;
  private final PersonDao personDao;
  private final CftDao cftDao;

  @Inject
  public AssessmentService(
      AssessmentDao assessmentDao, ConstructDao constructDao, PersonDao personDao, CftDao cftDao) {
    super(assessmentDao);
    this.constructDao = constructDao;
    this.personDao = personDao;
    this.cftDao = cftDao;
  }

  public Assessment start(StartAssessmentRequest request) {
    Require.requireNotNullAndNotEmpty(request);
    final Construct construct = fetchConstruct(request.getConstructId());
    final Person person = fetchPerson(request.getPersonId());
    final Cft cft = fetchCft(request.getCftId());

    final Assessment assessment = new Assessment();
    assessment.setJson(construct.getPrototype());
    assessment.setConstruct(construct);
    assessment.setPerson(person);
    assessment.setCft(cft);
    return this.create(assessment);
  }

  private Construct fetchConstruct(Long constructId) {
    Require.requireNotNullAndNotEmpty(constructId);
    return constructDao.find(constructId);
  }

  private Person fetchPerson(Long personId) {
    return personId != null ? personDao.find(personId) : null;
  }

  private Cft fetchCft(Long cftId) {
    return cftId != null ? cftDao.find(cftId) : null;
  }
}

package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.dao.CftDao;
import gov.ca.cwds.cans.dao.InstrumentDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.dto.assessment.StartAssessmentRequest;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Cft;
import gov.ca.cwds.cans.domain.entity.Instrument;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.search.SearchAssessmentPo;
import gov.ca.cwds.cans.util.Require;
import java.util.Collection;

/** @author denys.davydov */
public class AssessmentService extends AbstractCrudService<Assessment> {

  private final InstrumentDao instrumentDao;
  private final PersonDao personDao;
  private final CftDao cftDao;
  private final PerryService perryService;

  @Inject
  public AssessmentService(
      AssessmentDao assessmentDao,
      InstrumentDao instrumentDao,
      PersonDao personDao,
      CftDao cftDao,
      PerryService perryService) {
    super(assessmentDao);
    this.instrumentDao = instrumentDao;
    this.personDao = personDao;
    this.cftDao = cftDao;
    this.perryService = perryService;
  }

  @Override
  public Assessment create(Assessment assessment) {
    assessment.setCreatedBy(perryService.getOrPersistAndGetCurrentUser());
    return super.create(assessment);
  }

  @Override
  public Assessment update(Assessment assessment) {
    assessment.setUpdatedBy(perryService.getOrPersistAndGetCurrentUser());
    return super.update(assessment);
  }

  public Collection<Assessment> search(SearchAssessmentPo searchPo) {
    final Person currentUser = perryService.getOrPersistAndGetCurrentUser();
    searchPo.setCreatedById(currentUser.getId());
    return ((AssessmentDao) dao).search(searchPo);
  }

  public Assessment start(StartAssessmentRequest request) {
    Require.requireNotNullAndNotEmpty(request);
    final Instrument instrument = fetchInstrument(request.getInstrumentId());
    final Person person = fetchPerson(request.getPersonId());
    final Cft cft = fetchCft(request.getCftId());

    final Assessment assessment = new Assessment();
    assessment.setState(instrument.getPrototype());
    assessment.setAssessmentType(request.getAssessmentType());
    assessment.setStatus(AssessmentStatus.IN_PROGRESS);
    assessment.setCanReleaseConfidentialInfo(Boolean.FALSE);
    assessment.setInstrument(instrument);
    assessment.setInstrumentId(instrument.getId());
    assessment.setPerson(person);
    assessment.setCft(cft);
    assessment.setCreatedBy(perryService.getOrPersistAndGetCurrentUser());
    return this.create(assessment);
  }

  private Instrument fetchInstrument(Long instrumentId) {
    Require.requireNotNullAndNotEmpty(instrumentId);
    return instrumentDao.find(instrumentId);
  }

  private Person fetchPerson(Long personId) {
    return personId != null ? personDao.find(personId) : null;
  }

  private Cft fetchCft(Long cftId) {
    return cftId != null ? cftDao.find(cftId) : null;
  }
}

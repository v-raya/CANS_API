package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.mapper.ClientMapper;
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import gov.ca.cwds.rest.exception.ExpectedException;
import gov.ca.cwds.security.annotations.Authorize;
import java.util.Collection;
import java.util.Optional;
import javax.ws.rs.core.Response.Status;

/** @author denys.davydov */
public class AssessmentService extends AbstractCrudService<Assessment> {

  private final PerryService perryService;
  @Inject private PersonService personService;
  @Inject private ClientsService clientsService;
  @Inject private ClientMapper clientMapper;

  @Inject
  public AssessmentService(AssessmentDao assessmentDao, PerryService perryService) {
    super(assessmentDao); // NOSONAR
    this.perryService = perryService;
  }

  @Override
  public Assessment create(Assessment assessment) {
    assessment.setCreatedBy(perryService.getOrPersistAndGetCurrentUser());
    createClientIfNeeded(assessment);
    return super.create(assessment);
  }

  private void createClientIfNeeded(Assessment assessment) {
    String clientId = assessment.getPerson().getExternalId();
    final Person person = getPersonFromLegacy(clientId);
    Person persistedPerson = Optional.ofNullable(personService.findByExternalId(clientId))
        .orElseGet(() -> personService.create(person));
    assessment.setPerson(persistedPerson);
  }

  private Person getPersonFromLegacy(String clientId) {
    ClientDto client = clientsService.findByExternalId(clientId);
    if (client == null) {
      throw new ExpectedException(
          "Client [" + clientId + "] is not found in CWS/CMS database.", Status.BAD_REQUEST);
    }
    return clientMapper.toPerson(client);
  }

  @Override
  public Assessment update(Assessment assessment) {
    assessment.setUpdatedBy(perryService.getOrPersistAndGetCurrentUser());
    createClientIfNeeded(assessment);
    // TODO: design flow approach
    if (assessment.getStatus() == AssessmentStatus.COMPLETED) {
      Assessment existingAssessment = read(assessment.getId());
      if (existingAssessment.getStatus() != AssessmentStatus.COMPLETED) {
        return runCompleteFlow(assessment);
      }
    }
    return runUpdateFlow(assessment);
  }

  public Collection<Assessment> search(SearchAssessmentParameters searchAssessmentParameters) {
    return ((AssessmentDao) dao).search(searchAssessmentParameters);
  }

  public Collection<Assessment> getAssessmentsByCurrentUser() {
    return ((AssessmentDao) dao)
        .getAssessmentsByUserId(perryService.getOrPersistAndGetCurrentUser().getId());
  }

  Assessment runCompleteFlow(
      @Authorize("assessment:complete:assessment.id") Assessment assessment) {
    // TODO: design flow approach
    return super.update(assessment);
  }

  Assessment runUpdateFlow(@Authorize("assessment:update:assessment.id") Assessment assessment) {
    // TODO: design flow approach
    return super.update(assessment);
  }
}

package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.mapper.ClientMapper;
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import gov.ca.cwds.cans.security.assessment.AssessmentOperation;
import gov.ca.cwds.rest.exception.ExpectedException;
import java.util.Collection;
import java.util.Optional;
import javax.ws.rs.core.Response.Status;

/** @author denys.davydov */
public class AssessmentService extends AbstractCrudService<Assessment> {

  private final PerryService perryService;
  @Inject private PersonService personService;
  @Inject private ClientsService clientsService;
  @Inject private SecurityService securityService;
  @Inject private ClientMapper clientMapper;

  @Inject
  public AssessmentService(AssessmentDao assessmentDao, PerryService perryService) {
    super(assessmentDao); // NOSONAR
    this.perryService = perryService;
  }

  @Override
  public Assessment create(Assessment assessment) {
    Person assessmentPerson = assessment.getPerson();
    String clientId = assessmentPerson.getExternalId();
    ClientDto client = clientsService.findByExternalId(clientId);
    if (client == null) {
      throw new ExpectedException("Client is not found in CWS/CMS database.", Status.BAD_REQUEST);
    }
    Person person = clientMapper.toPerson(client);
    assessment.setPerson(person);
    assessment.setCreatedBy(perryService.getOrPersistAndGetCurrentUser());
    createClientIfNeeded(assessment);
    return super.create(assessment);
  }

  private void createClientIfNeeded(Assessment assessment) {
    assessment.setPerson(
        Optional.ofNullable(personService.findByExternalId(assessment.getPerson().getExternalId()))
            .orElseGet(() -> personService.create(assessment.getPerson())));
  }

  @Override
  public Assessment update(Assessment assessment) {
    String clientExternalId = assessment.getPerson().getExternalId();
    assessment.setPerson(
        Optional.ofNullable(personService.findByExternalId(clientExternalId))
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Can't find the client with externalId: " + clientExternalId)));
    assessment.setUpdatedBy(perryService.getOrPersistAndGetCurrentUser());
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

  private Assessment runCompleteFlow(Assessment assessment) {
    // TODO: design flow approach
    securityService.checkPermission(AssessmentOperation.complete.permission(assessment.getId()));
    return super.update(assessment);
  }

  private Assessment runUpdateFlow(Assessment assessment) {
    // TODO: design flow approach
    securityService.checkPermission(AssessmentOperation.update.permission(assessment.getId()));
    return super.update(assessment);
  }
}

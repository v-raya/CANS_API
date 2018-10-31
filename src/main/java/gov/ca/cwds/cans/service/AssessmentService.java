package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import java.util.Collection;
import java.util.Optional;

/** @author denys.davydov */
public class AssessmentService extends AbstractCrudService<Assessment> {

  private final PerryService perryService;
  @Inject private PersonService personService;

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
    return super.update(assessment);
  }

  public Collection<Assessment> search(SearchAssessmentParameters searchAssessmentParameters) {
    return ((AssessmentDao) dao).search(searchAssessmentParameters);
  }

  public Collection<Assessment> getAllAssessments() {
    return ((AssessmentDao) dao).getAllAssessments();
  }
}

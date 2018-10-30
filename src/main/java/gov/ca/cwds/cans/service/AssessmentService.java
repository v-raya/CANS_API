package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import java.util.Collection;

/** @author denys.davydov */
public class AssessmentService extends AbstractCrudService<Assessment> {

  private final PerryService perryService;

  @Inject
  public AssessmentService(AssessmentDao assessmentDao, PerryService perryService) {
    super(assessmentDao); // NOSONAR
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

  public Collection<Assessment> search(SearchAssessmentParameters searchAssessmentParameters) {
    return ((AssessmentDao) dao).search(searchAssessmentParameters);
  }

  public Collection<Assessment> getAllAssessments(
      SearchAssessmentParameters searchAssessmentParameters) {
    return ((AssessmentDao) dao).getAllAssessments(searchAssessmentParameters);
  }
}

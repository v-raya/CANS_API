package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.domain.entity.Assessment;

/**
 * @author denys.davydov
 */
public class AssessmentService extends AbstractCrudService<Assessment> {

  @Inject
  public AssessmentService(AssessmentDao assessmentDao) {
    super(assessmentDao);
  }

}

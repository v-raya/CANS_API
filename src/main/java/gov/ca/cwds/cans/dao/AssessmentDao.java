package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;

/**
 * @author denys.davydov
 */
public class AssessmentDao extends AbstractCrudDao<Assessment> {

  @Inject
  public AssessmentDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public Assessment create(Assessment assessment) {
    hibernateInitializeIfNeeded(assessment.getCft());
    hibernateInitializeIfNeeded(assessment.getInstrument());
    hibernateInitializeIfNeeded(assessment.getPerson());
    return super.create(assessment);
  }

  private void hibernateInitializeIfNeeded(Object o) {
    if (o != null) {
      Hibernate.initialize(o);
    }
  }
}

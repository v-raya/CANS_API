package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Instrument;
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
    hibernateInitializeIfNeeded(assessment.getPerson());
    hibernateInitializeIfNeeded(assessment.getInstrument());
    hibernateInitializeInstrument(assessment);
    return super.create(assessment);
  }

  @Override
  public Assessment update(Assessment assessment) {
    hibernateInitializeIfNeeded(assessment.getCft());
    hibernateInitializeIfNeeded(assessment.getPerson());
    hibernateInitializeIfNeeded(assessment.getInstrument());
    hibernateInitializeInstrument(assessment);
    return super.update(assessment);
  }

  private void hibernateInitializeIfNeeded(Object o) {
    if (o != null) {
      Hibernate.initialize(o);
    }
  }

  private void hibernateInitializeInstrument(Assessment assessment) {
    final Long instrumentId = assessment.getInstrumentId();
    if (instrumentId == null || assessment.getInstrument() != null) {
      return;
    }

    final Instrument instrument = new Instrument();
    instrument.setId(instrumentId);
    assessment.setInstrument(instrument);

    Hibernate.initialize(instrument);
  }
}

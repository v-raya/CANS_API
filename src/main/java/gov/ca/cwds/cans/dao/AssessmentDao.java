package gov.ca.cwds.cans.dao;

import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_PERSON_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_PERSON_ID;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Instrument;
import gov.ca.cwds.cans.domain.search.SearchAssessmentPo;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import java.util.Collection;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
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

  public Collection<Assessment> search(SearchAssessmentPo searchPo) {
    Require.requireNotNullAndNotEmpty(searchPo);
    final Session session = grabSession();
    addFilterIfNeeded(session, FILTER_CREATED_BY_ID, PARAM_CREATED_BY_ID, searchPo.getCreatedById());
    addFilterIfNeeded(session, FILTER_PERSON_ID, PARAM_PERSON_ID, searchPo.getPersonId());
    final List<Assessment> results = session
        .createNamedQuery(Assessment.NQ_ALL, Assessment.class)
        .list();
    return ImmutableList.copyOf(results);
  }

  private void addFilterIfNeeded(Session session, String filterName,
      String filterParameter, Object parameterValue) {
    if (parameterValue != null) {
      session.enableFilter(filterName)
          .setParameter(filterParameter, parameterValue);
    }
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

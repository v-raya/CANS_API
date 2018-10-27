package gov.ca.cwds.cans.dao;

import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_PERSON_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_PERSON_ID;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Instrument;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.security.annotations.Authorize;
import java.io.Serializable;
import java.util.Collection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/** @author denys.davydov */
public class AssessmentDao extends AbstractCrudDao<Assessment> {

  @Inject
  public AssessmentDao(
      @CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public void replaceCaseIds(final long personId, final long oldCaseId, final long newCaseId) {
    grabSession()
        .createQuery(
            "update Assessment set case_id = :newCaseId where person_id = :personId and case_id = :oldCaseId")
        .setParameter("personId", personId)
        .setParameter("oldCaseId", oldCaseId)
        .setParameter("newCaseId", newCaseId)
        .executeUpdate();
  }

  @Override
  public Assessment create(
      /*@Authorize({"person:write:assessment.person.id"})*/ Assessment assessment) {
    setCountyInitially(assessment);
    insertInstrumentById(assessment);
    return super.create(assessment);
  }

  private void setCountyInitially(final Assessment assessment) {
    final Person inputPerson = assessment.getPerson();
    Require.requireNotNullAndNotEmpty(inputPerson);
    Require.requireNotNullAndNotEmpty(inputPerson.getId());
    assessment.setCounty(inputPerson.getCounty());
  }

  private void insertInstrumentById(final Assessment assessment) {
    final Long instrumentId = assessment.getInstrumentId();
    if (instrumentId == null || assessment.getInstrument() != null) {
      return;
    }
    assessment.setInstrument(new Instrument().setId(instrumentId));
  }

  @Override
  public Assessment update(
      /*@Authorize({"person:write:assessment.person.id"})*/ Assessment assessment) {
    revertCountyToInitialValue(assessment);
    insertInstrumentById(assessment);
    return super.update(assessment);
  }

  private void revertCountyToInitialValue(Assessment assessment) {
    final Assessment previousState = super.find(assessment.getId());
    assessment.setCounty(previousState.getCounty());
  }

  /* Authorization going to be reworked
  @Override
  @Authorize({"person:read:result.person"})
  public Assessment find(Serializable primaryKey) {
    return super.find(primaryKey);
  }
  */

  /*@Authorize({"person:read:assessment.person"})*/
  public Collection<Assessment> search(SearchAssessmentParameters searchAssessmentParameters) {
    Require.requireNotNullAndNotEmpty(searchAssessmentParameters);
    final Session session = grabSession();
    addFilterIfNeeded(
        session,
        FILTER_CREATED_BY_ID,
        PARAM_CREATED_BY_ID,
        searchAssessmentParameters.getCreatedById());
    addFilterIfNeeded(
        session, FILTER_PERSON_ID, PARAM_PERSON_ID, searchAssessmentParameters.getPersonId());
    // returns List (and not ImmutableList as usual) to filter results with authorizer)
    return session.createNamedQuery(Assessment.NQ_ALL, Assessment.class).list();
  }

  private void addFilterIfNeeded(
      Session session, String filterName, String filterParameter, Object parameterValue) {
    if (parameterValue != null) {
      session.enableFilter(filterName).setParameter(filterParameter, parameterValue);
    }
  }
}

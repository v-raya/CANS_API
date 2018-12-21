package gov.ca.cwds.cans.dao;

import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_CREATED_UPDATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.FILTER_PERSON_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_CLIENT_IDENTIFIER;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_CREATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_CREATED_UPDATED_BY_ID;
import static gov.ca.cwds.cans.domain.entity.Assessment.PARAM_PERSON_ID;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Instrument;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.security.annotations.Authorize;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

/** @author denys.davydov */
public class AssessmentDao extends AbstractCrudDao<Assessment> {

  @Inject
  public AssessmentDao(@CansSessionFactory final SessionFactory sessionFactory) {
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
  public Assessment create(@Authorize("assessment:create:assessment") Assessment assessment) {
    setCountyInitially(assessment);
    insertInstrumentById(assessment);
    return super.create(assessment);
  }

  @Authorize("assessment:read:assessment")
  @Override
  public Assessment find(Serializable id) {
    return super.find(id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Assessment delete(@Authorize("assessment:delete:id") Serializable id) {
    //This 'hack' is needed for Envers audit table to have the status field = "DELETED"
    //for the delete operation record.
    Assessment assessment = super.find(id);
    if (assessment != null) {
      assessment.setStatus(AssessmentStatus.DELETED);
      super.update(assessment);
      super.grabSession().flush();
    }
    return super.delete(id);
  }

  private void insertInstrumentById(final Assessment assessment) {
    final Long instrumentId = assessment.getInstrumentId();
    if (instrumentId == null || assessment.getInstrument() != null) {
      return;
    }
    assessment.setInstrument(new Instrument().setId(instrumentId));
  }

  @Override
  public Assessment update(Assessment assessment) {
    revertCountyAndCaseIdToInitialValue(assessment);
    insertInstrumentById(assessment);
    return super.update(assessment);
  }

  private void revertCountyAndCaseIdToInitialValue(Assessment assessment) {
    final Assessment previousState = super.find(assessment.getId());
    assessment.setCounty(previousState.getCounty());
    assessment.setServiceSourceId(previousState.getServiceSourceId());
  }

  @Authorize("assessment:read:assessment")
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

    Query<Assessment> assessmentQuery =
        Optional.ofNullable(searchAssessmentParameters.getClientIdentifier())
            .map(
                clientIdentifier -> {
                  Query<Assessment> query =
                      session.createNamedQuery(searchAssessmentParameters.getIncludeDeleted() ?
                          Assessment.NQ_ALL_FOR_CLIENT_WITH_DELETED : Assessment.NQ_ALL_FOR_CLIENT,
                          Assessment.class);
                  query.setParameter(PARAM_CLIENT_IDENTIFIER, clientIdentifier);
                  return query;
                })
            .orElse(session.createNamedQuery(Assessment.NQ_ALL, Assessment.class));
    return assessmentQuery.list();
  }

  public Collection<Assessment> getAssessmentsByUserId(Long userId) {
    final Session session = grabSession();
    addFilterIfNeeded(session, FILTER_CREATED_UPDATED_BY_ID, PARAM_CREATED_UPDATED_BY_ID, userId);
    return session.createNamedQuery(Assessment.NQ_ALL, Assessment.class).list();
  }

  private void addFilterIfNeeded(
      Session session, String filterName, String filterParameter, Object parameterValue) {
    if (parameterValue != null) {
      session.enableFilter(filterName).setParameter(filterParameter, parameterValue);
    }
  }

  private void setCountyInitially(final Assessment assessment) {
    final Person inputPerson = assessment.getPerson();
    Require.requireNotNullAndNotEmpty(inputPerson);
    Require.requireNotNullAndNotEmpty(inputPerson.getId());
    assessment.setCounty(inputPerson.getCounty());
  }
}

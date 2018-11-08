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
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
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
    revertCountyAndCaseIdToInitialValue(assessment);
    insertInstrumentById(assessment);
    return super.update(assessment);
  }

  private void revertCountyAndCaseIdToInitialValue(Assessment assessment) {
    final Assessment previousState = super.find(assessment.getId());
    assessment.setCounty(previousState.getCounty());
    assessment.setCaseOrReferralId(previousState.getCaseOrReferralId());
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

    Query<Assessment> assessmentQuery =
        Optional.ofNullable(searchAssessmentParameters.getClientIdentifier())
            .map(
                clientIdentifier -> {
                  Query<Assessment> query =
                      session.createNamedQuery(Assessment.NQ_ALL_FOR_CLIENT, Assessment.class);
                  query.setParameter(PARAM_CLIENT_IDENTIFIER, clientIdentifier);
                  return query;
                })
            .orElse(session.createNamedQuery(Assessment.NQ_ALL, Assessment.class));
    return assessmentQuery.list();
  }

  // @Authorize({"person:read:assessment.person"})
  public Collection<Assessment> getAssessmentsByUserId(Long userId) {
    final Session session = grabSession();
    addFilterIfNeeded(session, FILTER_CREATED_UPDATED_BY_ID, PARAM_CREATED_UPDATED_BY_ID, userId);
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

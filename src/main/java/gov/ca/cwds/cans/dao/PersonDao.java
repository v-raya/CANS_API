package gov.ca.cwds.cans.dao;

import static gov.ca.cwds.cans.domain.entity.Person.PARAM_EXTERNAL_IDS;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import gov.ca.cwds.cans.Constants.Privileges;
import gov.ca.cwds.cans.domain.dto.person.PersonStatusDto;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.search.Pagination;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import gov.ca.cwds.cans.domain.search.SearchPersonResult;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.security.annotations.Authorize;
import gov.ca.cwds.security.utils.PrincipalUtils;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/** @author denys.davydov */
public class PersonDao extends AbstractCrudDao<Person> {

  @Inject
  public PersonDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public Person create(@Authorize("person:create:person") Person person) { // NOSONAR
    return super.create(person);
  }

  @Override
  @Authorize("person:read:person")
  public Person find(Serializable primaryKey) { // NOSONAR
    return super.find(primaryKey);
  }

  public Person findByExternalId(final String externalId) {
    Require.requireNotNullAndNotEmpty(externalId);
    final List<Person> people =
        this.grabSession()
            .createNamedQuery(Person.NQ_FIND_BY_EXTERNAL_ID, Person.class)
            .setParameter(Person.PARAM_EXTERNAL_ID, externalId)
            .list();
    return people.isEmpty() ? null : people.get(0);
  }

  public List<PersonStatusDto> findStatusesByExternalIds(Set<String> externalIds) {
    return this.grabSession()
        .createNamedQuery(Person.NQ_FIND_STATUSES_BY_EXTERNAL_IDS, PersonStatusDto.class)
        .setParameter(PARAM_EXTERNAL_IDS, externalIds)
        .list();
  }

  public SearchPersonResult search(final SearchPersonParameters searchParameters) {
    Require.requireNotNullAndNotEmpty(searchParameters);
    final Pagination pagination = searchParameters.getPagination();
    Require.requireNotNullAndNotEmpty(pagination);
    final Session session = prepareSession(searchParameters);
    final List<Person> people =
        session
            .createNamedQuery(Person.NQ_ALL, Person.class)
            .setFirstResult(pagination.getPage() * pagination.getPageSize())
            .setMaxResults(pagination.getPageSize())
            .list();
    final long totalRecords =
        (long) session.createNamedQuery(Person.NQ_COUNT_ALL).getSingleResult();
    return toSearchPersonResult(people, totalRecords);
  }

  private SearchPersonResult toSearchPersonResult(List<Person> people, long totalRecords) {
    return (SearchPersonResult)
        new SearchPersonResult()
            .setRecords(ImmutableList.copyOf(people))
            .setTotalRecords(totalRecords);
  }

  private Session prepareSession(SearchPersonParameters searchParameters) {
    final Session session = grabSession();
    authorize(session);
    enableFilters(searchParameters, session);
    enableLikeFilters(searchParameters, session);

    final PersonRole personRole = searchParameters.getPersonRole();
    if (personRole != null) {
      session
          .enableFilter(Person.FILTER_PERSON_ROLE)
          .setParameter(Person.PARAM_PERSON_ROLE, personRole.name());
    }

    final LocalDate dob = searchParameters.getDob();
    if (dob != null) {
      session.enableFilter(Person.FILTER_DOB).setParameter(Person.PARAM_DOB, dob);
    }
    return session;
  }

  private void enableFilters(SearchPersonParameters searchParameters, Session session) {
    enableFilter(
        session,
        Person.FILTER_EXTERNAL_ID,
        Person.PARAM_EXTERNAL_ID,
        searchParameters.getExternalId());
    enableFilter(
        session,
        Person.FILTER_COUNTY,
        Person.PARAM_USERS_COUNTY_EXTERNAL_ID,
        searchParameters.getUsersCountyExternalId());
  }

  private void enableFilter(
      final Session session, final String filter, final String filterParam, final String value) {
    if (StringUtils.isNotBlank(value)) {
      session.enableFilter(filter).setParameter(filterParam, value);
    }
  }

  private void enableLikeFilters(SearchPersonParameters searchParameters, Session session) {
    enableLikeFilter(
        session,
        Person.FILTER_FIRST_NAME,
        Person.PARAM_FIRST_NAME,
        searchParameters.getFirstName());
    enableLikeFilter(
        session,
        Person.FILTER_MIDDLE_NAME,
        Person.PARAM_MIDDLE_NAME,
        searchParameters.getMiddleName());
    enableLikeFilter(
        session, Person.FILTER_LAST_NAME, Person.PARAM_LAST_NAME, searchParameters.getLastName());
  }

  private void enableLikeFilter(
      final Session session, final String filter, final String filterParam, final String value) {
    if (StringUtils.isNotBlank(value)) {
      enableFilter(session, filter, filterParam, "%" + value.toLowerCase() + "%");
    }
  }

  private void authorize(final Session session) {
    if (!PrincipalUtils.getPrincipal().getPrivileges().contains(Privileges.SEALED)) {
      session.enableFilter(Person.AUTHORIZATION_FILTER);
    }
  }
}

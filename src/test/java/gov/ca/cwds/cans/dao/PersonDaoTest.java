package gov.ca.cwds.cans.dao;

import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import gov.ca.cwds.cans.util.NullOrEmptyException;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PersonDaoTest {

  @Test(expected = NullOrEmptyException.class)
  public void search_errors_withNullSearch() {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    PersonDao personDao = new PersonDao(sessionFactory);
    personDao.search(null, "");
  }

  @Test(expected = NullOrEmptyException.class)
  public void search_errors_withNullCountyId() {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    SearchPersonParameters searchPersonParameters = new SearchPersonParameters();
    searchPersonParameters.setExternalId("22");
    PersonDao personDao = new PersonDao(sessionFactory);
    personDao.search(searchPersonParameters, null);
  }

  @Test
  public void search_setsJustCountyFilter_withNoRoleOrExternalId() throws Exception {
    List<Person> people = Collections.singletonList(new Person());
    SessionFactory sessionFactory = mock(SessionFactory.class);
    Session session = mock(Session.class);
    Query<Person> query = mock(Query.class);
    Filter countyFilter = mock(Filter.class);
    Mockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
    Mockito.when(session.enableFilter(Person.FILTER_COUNTY)).thenReturn(countyFilter);
    Mockito.when(session.createNamedQuery(Person.NQ_ALL, Person.class)).thenReturn(query);
    Mockito.when(query.list()).thenReturn(people);

    PersonDao personDao = new PersonDao(sessionFactory);
    SearchPersonParameters searchPersonParameters = new SearchPersonParameters();
    searchPersonParameters.setExternalId("");
    personDao.search(searchPersonParameters, "11");

    verify(session).enableFilter(Person.FILTER_COUNTY);
    verify(countyFilter).setParameter("external_id", "11");
  }
}

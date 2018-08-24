package gov.ca.cwds.cans.dao;

import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import gov.ca.cwds.cans.util.NullOrEmptyException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class PersonDaoTest {

  @Test(expected = NullOrEmptyException.class)
  public void testSearchWithNullSearch() {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    PersonDao personDao = new PersonDao(sessionFactory);
    personDao.search(null);
  }

  @Test
  public void testSearchWithNoFilters() throws Exception {
    List<Person> people = Collections.singletonList(new Person());
    SessionFactory sessionFactory = mock(SessionFactory.class);
    Session session = mock(Session.class);
    Query<Person> query = mock(Query.class);
    Mockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
    Mockito.when(session.createNamedQuery(Person.NQ_ALL, Person.class)).thenReturn(query);
    Mockito.when(query.list()).thenReturn(people);

    PersonDao personDao = new PersonDao(sessionFactory);
    SearchPersonPo searchPersonPo = new SearchPersonPo();
    searchPersonPo.setExternalId("");
    assertThat(personDao.search(searchPersonPo), is(people));
  }
}

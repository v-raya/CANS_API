package gov.ca.cwds.cans.dao;

import static junit.framework.TestCase.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.Pagination;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import gov.ca.cwds.cans.util.NullOrEmptyException;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import java.util.Collections;
import java.util.List;
import org.hibernate.Filter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(gov.ca.cwds.security.utils.PrincipalUtils.class)
public class PersonDaoTest {

  PersonDao personDao;
  SessionFactory sessionFactory;
  Session session;

  @Before
  public void setup() {
    sessionFactory = mock(SessionFactory.class);
    session = mock(Session.class);
    when(sessionFactory.getCurrentSession()).thenReturn(session);
    personDao = new PersonDao(sessionFactory);
  }

  @Test
  public void saveOrUpdateShouldCreatePerson() {
    personDao.create(new Person());
    verify(sessionFactory).getCurrentSession();
    verify(session).saveOrUpdate(any());
  }
  @Test
  public void saveOrUpdateShouldNotCreateAndOrDoSomethingWhenPersonIsNull() {
    personDao.create(null);
    verify(sessionFactory).getCurrentSession();
    verify(session).saveOrUpdate(any());
  }

  @Test
  public void findShouldFindPersonByPrimaryKey() {
    personDao.find("123");
    verify(sessionFactory).getCurrentSession();
    //have to fiddle around with types
    //verify(session).get(any(), "123");

  }

  @Test(expected = HibernateException.class)
  public void findShouldThrowExceptionWhenFindingWithAMissingId() {
    personDao.find("");
    verify(sessionFactory).getCurrentSession();
  }

  @Test(expected = NullOrEmptyException.class)
  public void findByExternalIdShouldThrowExceptionWhenIdIsEmpty() {
    Person foundPerson = personDao.findByExternalId("");
  }

  @Test
  public void findByExternalIdShouldFindByExternalId() {
    Query query = mock(Query.class);
    when(session.createNamedQuery(any(), any())).thenReturn(query);
    when(query.setParameter(Person.PARAM_EXTERNAL_ID, "123")).thenReturn(query);
    Person foundPerson = personDao.findByExternalId("123");
    verify(query).list();

  }

  @Test
  public void findByExternalIdShouldReturnNullWhenFindingPeopleByExternalIdReturnsNoResults() {
    Person foundPerson = personDao.findByExternalId("nonExistingId");
    assertNull(foundPerson);
  }

  @Test(expected = NullOrEmptyException.class)
  public void searchShouldThrowExceptionWhenNullIsParam() {
    personDao.search(null);
  }

  //The following tests are more difficult because the class is doing to many things.
  //The inner workings are leaked out and the dao knows too much about these classes.
  //By extracting logic into other classes and delegating the work to those, the testing
  // can be greatly reduced and better reuse and patterns are established.
  //Consider pulling out Filters, session, and auth into auxilary classes.
  @Test
  public void searchShouldThrowErrorWhenSearchParamIsEmpty() {

  }

  @Test
  public void searchShouldReturnSearchPersonResults(){

  }

  @Test
  public void searchShouldPaginate(){

  }

  @Test
  public void searchShouldLimitResults(){

  }

  //This is a big test because it is doing lots of work with session, filters, and authorizations.
  //Test can be simplified by pulling these code out.
//  @Test
//  public void search_setsJustCountyFilter_withNoRoleOrExternalIdOrAuthorization() {
//    List<Person> people = Collections.singletonList(new Person());
//    SessionFactory sessionFactory = mock(SessionFactory.class);
//    Session session = mock(Session.class);
//    Query<Person> query = mock(Query.class);
//    Filter countyFilter = mock(Filter.class);
//    PerryAccount perryAccount = new PerryAccount();
//    perryAccount.setPrivileges(Collections.emptySet());
//    mockStatic(PrincipalUtils.class);
//    when(sessionFactory.getCurrentSession()).thenReturn(session);
//    when(session.enableFilter(Person.FILTER_COUNTY)).thenReturn(countyFilter);
//    when(session.createNamedQuery(Person.NQ_ALL, Person.class)).thenReturn(query);
//    when(query.setFirstResult(0)).thenReturn(query);
//    when(query.setMaxResults(10)).thenReturn(query);
//    when(query.list()).thenReturn(people);
//
//    Query<Long> query2 = mock(Query.class);
//    when(session.createNamedQuery(Person.NQ_COUNT_ALL)).thenReturn(query2);
//    when(query2.getSingleResult()).thenReturn(0L);
//    when(PrincipalUtils.getPrincipal()).thenReturn(perryAccount);
//
//    PersonDao personDao = new PersonDao(sessionFactory);
//    SearchPersonParameters searchPersonParameters =
//        new SearchPersonParameters()
//            .setUsersCountyExternalId("11")
//            .setPagination(new Pagination().setPage(0).setPageSize(10));
//    personDao.search(searchPersonParameters);
//
//    verify(session).enableFilter(Person.FILTER_COUNTY);
//    verify(countyFilter).setParameter(Person.PARAM_USERS_COUNTY_EXTERNAL_ID, "11");
//  }
}

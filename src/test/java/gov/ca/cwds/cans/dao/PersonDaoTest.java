package gov.ca.cwds.cans.dao;

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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(gov.ca.cwds.security.utils.PrincipalUtils.class)
public class PersonDaoTest {

  @Test(expected = NullOrEmptyException.class)
  public void search_errors_withNullSearch() {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    PersonDao personDao = new PersonDao(sessionFactory);
    personDao.search(null);
  }

  @Test
  public void search_setsJustCountyFilter_withNoRoleOrExternalIdOrAuthorization() {
    List<Person> people = Collections.singletonList(new Person());
    SessionFactory sessionFactory = mock(SessionFactory.class);
    Session session = mock(Session.class);
    Query<Person> query = mock(Query.class);
    Filter countyFilter = mock(Filter.class);
    PerryAccount perryAccount = new PerryAccount();
    perryAccount.setPrivileges(Collections.emptySet());
    mockStatic(PrincipalUtils.class);
    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(session.enableFilter(Person.FILTER_COUNTY)).thenReturn(countyFilter);
    when(session.createNamedQuery(Person.NQ_ALL, Person.class)).thenReturn(query);
    when(query.setFirstResult(0)).thenReturn(query);
    when(query.setMaxResults(10)).thenReturn(query);
    when(query.list()).thenReturn(people);

    Query<Long> query2 = mock(Query.class);
    when(session.createNamedQuery(Person.NQ_COUNT_ALL)).thenReturn(query2);
    when(query2.getSingleResult()).thenReturn(0L);
    when(PrincipalUtils.getPrincipal()).thenReturn(perryAccount);

    PersonDao personDao = new PersonDao(sessionFactory);
    SearchPersonParameters searchPersonParameters =
        new SearchPersonParameters()
            .setUsersCountyExternalId("11")
            .setPagination(new Pagination().setPage(0).setPageSize(10));
    personDao.search(searchPersonParameters);

    verify(session).enableFilter(Person.FILTER_COUNTY);
    verify(countyFilter).setParameter(Person.PARAM_USERS_COUNTY_EXTERNAL_ID, "11");
  }
}

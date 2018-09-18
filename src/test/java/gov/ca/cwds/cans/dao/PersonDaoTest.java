package gov.ca.cwds.cans.dao;

import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import gov.ca.cwds.cans.util.NullOrEmptyException;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

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
  public void search_setsJustCountyFilter_withNoRoleOrExternalIdOrAuthorization() throws Exception {
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
    when(query.list()).thenReturn(people);
    when(PrincipalUtils.getPrincipal()).thenReturn(perryAccount);

    PersonDao personDao = new PersonDao(sessionFactory);
    SearchPersonParameters searchPersonParameters = new SearchPersonParameters();
    searchPersonParameters.setExternalId("");
    searchPersonParameters.setUsersCountyExternalId("11");
    personDao.search(searchPersonParameters);

    verify(session).enableFilter(Person.FILTER_COUNTY);
    verify(countyFilter).setParameter(Person.PARAM_USERS_COUNTY_EXTERNAL_ID, "11");
  }
}

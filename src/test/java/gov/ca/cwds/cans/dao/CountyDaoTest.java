package gov.ca.cwds.cans.dao;

import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.util.NullOrEmptyException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CountyDaoTest {

  @Test(expected = NullOrEmptyException.class)
  public void testfindByExternalIdWithNullSearch() {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    CountyDao countyDao = new CountyDao(sessionFactory);
    countyDao.findByExternalId(null);
  }

  @Test
  public void testFindByExternalIdWithId() {
    County county = new County();
    SessionFactory sessionFactory = mock(SessionFactory.class);
    Session session = mock(Session.class);
    SimpleNaturalIdLoadAccess simpleNaturalIdLoadAccess = mock(SimpleNaturalIdLoadAccess.class);
    Mockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
    Mockito.when(session.bySimpleNaturalId(County.class)).thenReturn(simpleNaturalIdLoadAccess);
    Mockito.when(simpleNaturalIdLoadAccess.load("42")).thenReturn(county);

    CountyDao countyDao = new CountyDao(sessionFactory);
    assertThat(countyDao.findByExternalId("42"), is(county));
  }
}
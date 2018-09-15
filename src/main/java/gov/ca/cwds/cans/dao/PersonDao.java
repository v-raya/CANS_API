package gov.ca.cwds.cans.dao;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.List;

/** @author denys.davydov */
public class PersonDao extends AbstractCrudDao<Person> {

  @Inject
  public PersonDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Collection<Person> search(SearchPersonPo searchPo, String countyId) {
    Require.requireNotNullAndNotEmpty(searchPo);
    Require.requireNotNullAndNotEmpty(countyId);

    final Session session = grabSession();
    final PersonRole personRole = searchPo.getPersonRole();
    session.enableFilter(Person.FILTER_COUNTY).setParameter("external_id", countyId);
    if (personRole != null) {
      session.enableFilter(Person.FILTER_PERSON_ROLE)
          .setParameter(Person.PARAM_PERSON_ROLE, personRole.name());
    }
    final String externalId = searchPo.getExternalId();
    if (StringUtils.isNotBlank(externalId)) {
      session.enableFilter(Person.FILTER_EXTERNAL_ID)
          .setParameter(Person.PARAM_EXTERNAL_ID, externalId);
    }

    final List<Person> results = session.createNamedQuery(Person.NQ_ALL, Person.class).list();
    return ImmutableList.copyOf(results);
  }
}

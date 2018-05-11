package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import org.hibernate.SessionFactory;

/** @author denys.davydov */
public class PersonDao extends AbstractCrudDao<Person> {

  @Inject
  public PersonDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }
}

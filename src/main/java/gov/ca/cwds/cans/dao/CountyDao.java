package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import org.hibernate.SessionFactory;

/** @author denys.davydov */
public class CountyDao extends AbstractCrudDao<County> {

  @Inject
  public CountyDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }
}

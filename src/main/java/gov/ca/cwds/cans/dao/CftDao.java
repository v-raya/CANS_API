package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Cft;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import org.hibernate.SessionFactory;

/** @author denys.davydov */
public class CftDao extends BaseDaoImpl<Cft> {

  @Inject
  public CftDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }
}

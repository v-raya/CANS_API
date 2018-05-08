package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Construct;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;

/** @author denys.davydov */
public class ConstructDao extends BaseDaoImpl<Construct> {

  @Inject
  public ConstructDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public Construct create(Construct construct) {
    final County county = construct.getCounty();
    if (county != null) {
      Hibernate.initialize(county);
    }

    return super.create(construct);
  }
}

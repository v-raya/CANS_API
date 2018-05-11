package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.entity.Instrument;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;

/** @author denys.davydov */
public class InstrumentDao extends AbstractCrudDao<Instrument> {

  @Inject
  public InstrumentDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public Instrument create(Instrument instrument) {
    final County county = instrument.getCounty();
    if (county != null) {
      Hibernate.initialize(county);
    }

    return super.create(instrument);
  }

  @Override
  public Instrument update(Instrument instrument) {
    final County county = instrument.getCounty();
    if (county != null) {
      Hibernate.initialize(county);
    }

    return super.update(instrument);
  }
}

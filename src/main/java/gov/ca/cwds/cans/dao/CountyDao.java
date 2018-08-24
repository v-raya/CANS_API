package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import org.hibernate.SessionFactory;

/**
 * @author denys.davydov
 */
public class CountyDao extends AbstractCrudDao<County> {

  @Inject
  public CountyDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public County findByExternalId(String externalId) {
    Require.requireNotNullAndNotEmpty(externalId);
    return grabSession().bySimpleNaturalId(County.class).load(externalId);
  }
}

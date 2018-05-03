package gov.ca.cwds.cans.inject;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import gov.ca.cwds.cans.CansConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.SessionFactoryFactory;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Bootstrap;
import org.hibernate.SessionFactory;

/** @author denys.davydov */
public class DataAccessModule extends AbstractModule {

  private final ImmutableList<Class<?>> entities =
      ImmutableList.<Class<?>>builder()
          .add()
          .build();

  private final HibernateBundle<CansConfiguration> hibernateBundle =
      new HibernateBundle<CansConfiguration>(entities, new SessionFactoryFactory()) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(CansConfiguration configuration) {
          return configuration.getDataSourceFactory();
        }

        @Override
        public String name() {
          return CANS;
        }
      };

  public DataAccessModule(Bootstrap<? extends CansConfiguration> bootstrap) {
    bootstrap.addBundle(hibernateBundle);
  }

  @Override
  protected void configure() {
    // do nothing
  }

  @Provides
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory() {
    return new UnitOfWorkAwareProxyFactory(getHibernateBundle());
  }

  @Provides
  @CansSessionFactory
  SessionFactory cmsSessionFactory() {
    return hibernateBundle.getSessionFactory();
  }

  @Provides
  @CansHibernateBundle
  public HibernateBundle<CansConfiguration> getHibernateBundle() {
    return hibernateBundle;
  }
}

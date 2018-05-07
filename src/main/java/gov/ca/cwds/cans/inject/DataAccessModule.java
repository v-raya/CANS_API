package gov.ca.cwds.cans.inject;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import gov.ca.cwds.cans.CansConfiguration;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Cft;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.entity.I18n;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.entity.Template;
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
          .add(
              Assessment.class,
              Cft.class,
              County.class,
              I18n.class,
              Person.class,
              Template.class
          )
          .build();

  private final HibernateBundle<CansConfiguration> hibernateBundle =
      new HibernateBundle<CansConfiguration>(entities, new SessionFactoryFactory()) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(CansConfiguration configuration) {
          return configuration.getCansDataSourceFactory();
        }

        @Override
        public String name() {
          return CANS;
        }

        @Override
        public void configure(org.hibernate.cfg.Configuration configuration) {
          configuration.addPackage("gov.ca.cwds.cans.domain.entity");
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

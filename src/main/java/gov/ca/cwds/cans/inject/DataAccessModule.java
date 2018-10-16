package gov.ca.cwds.cans.inject;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS_RS;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import gov.ca.cwds.cans.CansConfiguration;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Case;
import gov.ca.cwds.cans.domain.entity.Cft;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.entity.I18n;
import gov.ca.cwds.cans.domain.entity.Instrument;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.inject.CmsHibernateBundle;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.inject.CwsRsHibernateBundle;
import gov.ca.cwds.inject.CwsRsSessionFactory;
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
              Case.class,
              Cft.class,
              County.class,
              I18n.class,
              Person.class,
              Instrument.class)
          .build();

  private final ImmutableList<Class<?>> cmsEntities = ImmutableList.<Class<?>>builder().build();

  private final ImmutableList<Class<?>> cmsRsEntities = ImmutableList.<Class<?>>builder().build();

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

  private final HibernateBundle<CansConfiguration> cmsHibernateBundle =
      new HibernateBundle<CansConfiguration>(cmsEntities, new SessionFactoryFactory()) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(CansConfiguration configuration) {
          return configuration.getCmsDataSourceFactory();
        }

        @Override
        public String name() {
          return CMS;
        }
      };

  private final HibernateBundle<CansConfiguration> cmsRsHibernateBundle =
      new HibernateBundle<CansConfiguration>(cmsRsEntities, new SessionFactoryFactory()) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(CansConfiguration configuration) {
          return configuration.getCmsRsDataSourceFactory();
        }

        @Override
        public String name() {
          return CMS_RS;
        }
      };

  public DataAccessModule(Bootstrap<? extends CansConfiguration> bootstrap) {
    bootstrap.addBundle(hibernateBundle);
    bootstrap.addBundle(cmsHibernateBundle);
    bootstrap.addBundle(cmsRsHibernateBundle);
  }

  @Override
  protected void configure() {
    // do nothing
  }

  @Provides
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory() {
    return new UnitOfWorkAwareProxyFactory(
        getHibernateBundle(), getCmsHibernateBundle(), getCmsRsHibernateBundle());
  }

  @Provides
  @CansSessionFactory
  SessionFactory cansSessionFactory() {
    return hibernateBundle.getSessionFactory();
  }

  @Provides
  @CmsSessionFactory
  SessionFactory cmsSessionFactory() {
    return cmsHibernateBundle.getSessionFactory();
  }

  @Provides
  @CwsRsSessionFactory
  SessionFactory cmsRsSessionFactory() {
    return cmsRsHibernateBundle.getSessionFactory();
  }

  @Provides
  @CansHibernateBundle
  public HibernateBundle<CansConfiguration> getHibernateBundle() {
    return hibernateBundle;
  }

  @Provides
  @CmsHibernateBundle
  public HibernateBundle<CansConfiguration> getCmsHibernateBundle() {
    return cmsHibernateBundle;
  }

  @Provides
  @CwsRsHibernateBundle
  public HibernateBundle<CansConfiguration> getCmsRsHibernateBundle() {
    return cmsRsHibernateBundle;
  }
}

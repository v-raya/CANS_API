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
import gov.ca.cwds.cans.domain.entity.envers.NsRevisionEntity;
import gov.ca.cwds.data.legacy.cms.entity.BackgroundCheck;
import gov.ca.cwds.data.legacy.cms.entity.CaseAssignment;
import gov.ca.cwds.data.legacy.cms.entity.CaseLoad;
import gov.ca.cwds.data.legacy.cms.entity.CaseLoadWeighting;
import gov.ca.cwds.data.legacy.cms.entity.ChildClient;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.legacy.cms.entity.ClientOtherEthnicity;
import gov.ca.cwds.data.legacy.cms.entity.CountyLicenseCase;
import gov.ca.cwds.data.legacy.cms.entity.LicensingIssue;
import gov.ca.cwds.data.legacy.cms.entity.LicensingVisit;
import gov.ca.cwds.data.legacy.cms.entity.LongText;
import gov.ca.cwds.data.legacy.cms.entity.OtherAdultsInPlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.OtherChildrenInPlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.OtherPeopleScpRelationship;
import gov.ca.cwds.data.legacy.cms.entity.OutOfHomePlacement;
import gov.ca.cwds.data.legacy.cms.entity.OutOfStateCheck;
import gov.ca.cwds.data.legacy.cms.entity.PlacementEpisode;
import gov.ca.cwds.data.legacy.cms.entity.PlacementFacilityTypeHistory;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeNotes;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeProfile;
import gov.ca.cwds.data.legacy.cms.entity.Referral;
import gov.ca.cwds.data.legacy.cms.entity.ReferralAssignment;
import gov.ca.cwds.data.legacy.cms.entity.StaffPerson;
import gov.ca.cwds.data.legacy.cms.entity.StaffPersonCaseLoad;
import gov.ca.cwds.data.legacy.cms.entity.SubstituteCareProvider;
import gov.ca.cwds.data.legacy.cms.entity.facade.CaseByStaff;
import gov.ca.cwds.data.legacy.cms.entity.facade.ClientByStaff;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.ActiveServiceComponentType;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.ApprovalStatusType;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.CaseClosureReasonType;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.Country;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.NameType;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.SecondaryAssignmentRoleType;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.SystemCode;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.VisitType;
import gov.ca.cwds.inject.CmsSessionFactory;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.SessionFactoryFactory;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Bootstrap;
import org.hibernate.SessionFactory;

/**
 * @author denys.davydov
 */
public class DataAccessModule extends AbstractModule {

  private final ImmutableList<Class<?>> entities =
      ImmutableList.<Class<?>>builder()
          .add(
              NsRevisionEntity.class,
              Assessment.class,
              Case.class,
              Cft.class,
              County.class,
              I18n.class,
              Person.class,
              Instrument.class)
          .build();

  private final ImmutableList<Class<?>> cmsEntities =
      ImmutableList.<Class<?>>builder()
          .add(
              ActiveServiceComponentType.class,
              ApprovalStatusType.class,
              BackgroundCheck.class,
              CaseByStaff.class,
              gov.ca.cwds.data.legacy.cms.entity.Case.class,
              CaseAssignment.class,
              CaseClosureReasonType.class,
              CaseLoad.class,
              CaseLoadWeighting.class,
              ChildClient.class,
              Client.class,
              ClientOtherEthnicity.class,
              gov.ca.cwds.data.legacy.cms.entity.syscodes.County.class,
              County.class,
              CountyLicenseCase.class,
              Country.class,
              LongText.class,
              LicensingVisit.class,
              NameType.class,
              OutOfHomePlacement.class,
              OutOfStateCheck.class,
              OtherChildrenInPlacementHome.class,
              OtherAdultsInPlacementHome.class,
              OtherPeopleScpRelationship.class,
              PlacementEpisode.class,
              PlacementHome.class,
              PlacementHomeProfile.class,
              PlacementHomeNotes.class,
              PlacementFacilityTypeHistory.class,
              ReferralAssignment.class,
              Referral.class,
              StaffPerson.class,
              SecondaryAssignmentRoleType.class,
              StaffPersonCaseLoad.class,
              SubstituteCareProvider.class,
              VisitType.class,
              LicensingIssue.class,
              ClientByStaff.class,
              SystemCode.class)
          .build();

  private final ImmutableList<Class<?>> cmsRsEntities = ImmutableList.<Class<?>>builder().build();

  private final HibernateBundle<CansConfiguration> cansHibernateBundle =
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
    bootstrap.addBundle(cansHibernateBundle);
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
        cansHibernateBundle, cmsHibernateBundle, cmsRsHibernateBundle);
  }

  @Provides
  @CansSessionFactory
  SessionFactory cansSessionFactory() {
    return cansHibernateBundle.getSessionFactory();
  }

  @Provides
  @CmsSessionFactory
  SessionFactory cmsSessionFactory() {
    return cmsHibernateBundle.getSessionFactory();
  }
}

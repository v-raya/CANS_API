package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Case;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import java.util.List;
import org.hibernate.SessionFactory;

/** @author denys.davydov */
public class CaseDao extends AbstractCrudDao<Case> {

  @Inject
  public CaseDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Case findByExternalId(final String externalId) {
    Require.requireNotNullAndNotEmpty(externalId);
    final List<Case> resultList =
        grabSession()
            .createNamedQuery(Case.NQ_FIND_BY_EXTERNAL_ID, Case.class)
            .setParameter(Case.NQ_PARAM_EXTERNAL_ID, externalId)
            .getResultList();
    return resultList.isEmpty() ? null : resultList.get(0);
  }

  public void findByExternalIdOrCreate(final Case aCase, final Person currentUser) {
    final Case caseByExternalId = this.findByExternalId(aCase.getExternalId());
    if (caseByExternalId != null) {
      aCase
          .setId(caseByExternalId.getId())
          .setCreatedBy(caseByExternalId.getCreatedBy())
          .setCreatedTimestamp(caseByExternalId.getCreatedTimestamp());
    } else {
      aCase.setId(null).setCreatedBy(currentUser);
      this.create(aCase);
    }
  }

  public void createOrReplace(final Case aCase, final Person currentUser) {
    if (aCase.getId() == null) {
      aCase.setCreatedBy(currentUser);
      this.create(aCase);
      return;
    }

    findByExternalIdOrCreate(aCase, currentUser);
  }
}

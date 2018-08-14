package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Case;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

  /**
   * The purpose of the method is to reuse cases records by externalId field.
   * The method finds a case by externalId of the input case and updates the input case if found.
   * The old case id to the new case id pair is returned then.
   *
   * If no case found - a new record in db will be created and the input case will be updated.
   *
   * @param aCase - the case object that will be updated by the method
   * @param currentUser - currently logged in user, used when new case record is created
   * @return collection of pairs (old case id to new case id). Empty list if no old cases were involved
   */
  public Collection<Pair<Long, Long>> findByExternalIdOrCreate(
      final Case aCase, final Person currentUser) {
    final List<Pair<Long, Long>> updatedCasesIds = new ArrayList<>();
    final Case caseByExternalId = this.findByExternalId(aCase.getExternalId());
    if (caseByExternalId != null) {
      if (!caseByExternalId.getId().equals(aCase.getId())) {
        if (aCase.getId() != null) {
          updatedCasesIds.add(new ImmutablePair<>(aCase.getId(), caseByExternalId.getId()));
        }
        aCase.setId(caseByExternalId.getId());
      }
      aCase
          .setCreatedBy(caseByExternalId.getCreatedBy())
          .setCreatedTimestamp(caseByExternalId.getCreatedTimestamp());
    } else {
      final Long oldId = aCase.getId();
      aCase.setId(null).setCreatedBy(currentUser);
      this.create(aCase);
      if (oldId != null) {
        updatedCasesIds.add(new ImmutablePair<>(oldId, aCase.getId()));
      }
    }
    grabSession().flush();
    return updatedCasesIds;
  }

  public Collection<Pair<Long, Long>> createOrReplace(final Case aCase, final Person currentUser) {
    if (aCase.getId() == null) {
      aCase.setCreatedBy(currentUser);
      this.create(aCase);
      return CollectionUtils.emptyCollection();
    }

    return findByExternalIdOrCreate(aCase, currentUser);
  }
}

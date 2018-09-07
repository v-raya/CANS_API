package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.dao.CaseDao;
import gov.ca.cwds.cans.dao.CountyDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.Case;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;

/** @author denys.davydov */
public class PersonService extends AbstractCrudService<Person> {

  private final CountyDao countyDao;
  private final CaseDao caseDao;
  private final AssessmentDao assessmentDao;
  private final PerryService perryService;

  @Inject
  public PersonService(
      final PersonDao dao,
      final CountyDao countyDao,
      final CaseDao caseDao,
      final AssessmentDao assessmentDao,
      final PerryService perryService) {
    super(dao); // NOSONAR
    this.countyDao = countyDao;
    this.caseDao = caseDao;
    this.assessmentDao = assessmentDao;
    this.perryService = perryService;
  }

  public Collection<Person> findAll() {
    return dao.findAll();
  }

  public Collection<Person> search(final SearchPersonPo searchPo) {
    final PerryAccount perryAccount = PrincipalUtils.getPrincipal();
    final String countyId = countyDao.findByExternalId(perryAccount.getCountyCwsCode()).getId().toString();
    return ((PersonDao) dao).search(searchPo, countyId);
  }

  @Override
  public Person create(final Person person) {
    Require.requireNotNullAndNotEmpty(person);
    initializeCasesForCreate(person);
    return super.create(person);
  }

  private void initializeCasesForCreate(final Person person) {
    final List<Case> cases = person.getCases();
    if (CollectionUtils.isEmpty(cases)) {
      return;
    }

    final Person currentUser = perryService.getOrPersistAndGetCurrentUser();
    cases.forEach(aCase -> caseDao
        .findByExternalIdOrCreate(aCase, currentUser)
        .forEach(pair -> assessmentDao.replaceCaseIds(person.getId(), pair.getLeft(), pair.getRight()))
    );
  }

  @Override
  public Person update(final Person person) {
    Require.requireNotNullAndNotEmpty(person);
    initializeCasesForUpdate(person);
    return super.update(person);
  }

  private void initializeCasesForUpdate(final Person person) {
    final List<Case> cases = person.getCases();
    if (CollectionUtils.isEmpty(cases)) {
      return;
    }

    final Person currentUser = perryService.getOrPersistAndGetCurrentUser();
    cases.forEach(aCase -> caseDao
        .createOrReplace(aCase, currentUser)
        .forEach(pair -> assessmentDao.replaceCaseIds(person.getId(), pair.getLeft(), pair.getRight()))
    );
  }
}

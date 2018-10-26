package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.dao.CaseDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.domain.entity.Case;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import gov.ca.cwds.cans.domain.search.SearchPersonResult;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.security.annotations.Authorize;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

/** @author denys.davydov */
public class PersonService extends AbstractCrudService<Person> {

  private CaseDao caseDao;
  private AssessmentDao assessmentDao;
  private PerryService perryService;

  public PersonService(
      PersonDao dao, CaseDao caseDao, AssessmentDao assessmentDao, PerryService perryService) {
    super(dao);
    this.caseDao = caseDao;
    this.assessmentDao = assessmentDao;
    this.perryService = perryService;
  }

  @Inject
  public void setDao(final PersonDao dao) {
    this.dao = dao;
  }

  public SearchPersonResult search(final SearchPersonParameters searchPersonParameters) {
    final PerryAccount perryAccount = PrincipalUtils.getPrincipal();
    searchPersonParameters.setUsersCountyExternalId(perryAccount.getCountyCwsCode());
    return ((PersonDao) dao).search(searchPersonParameters);
  }

  public Person findByExternalId(final String externalId) {
    return ((PersonDao) dao).findByExternalId(externalId);
  }

  @Override
  public Person create(final Person person) {
    Require.requireNotNullAndNotEmpty(person);
    initializeCasesForCreate(person);
    return super.create(person);
  }

  @UnitOfWork(CANS)
  public List<StaffClientDto> findStatusesByExternalIds(Set<String> externalIds) {
    return ((PersonDao) dao).findStatusesByExternalIds(externalIds);
  }

  private void initializeCasesForCreate(final Person person) {
    final List<Case> cases = person.getCases();
    if (CollectionUtils.isEmpty(cases)) {
      return;
    }

    final Person currentUser = perryService.getOrPersistAndGetCurrentUser();
    cases.forEach(
        aCase ->
            caseDao
                .findByExternalIdOrCreate(aCase, currentUser)
                .forEach(
                    pair ->
                        assessmentDao.replaceCaseIds(
                            person.getId(), pair.getLeft(), pair.getRight())));
  }

  @Override
  public Person update(@Authorize("person:write:person.id") final Person person) {
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
    cases.forEach(
        aCase ->
            caseDao
                .createOrReplace(aCase, currentUser)
                .forEach(
                    pair ->
                        assessmentDao.replaceCaseIds(
                            person.getId(), pair.getLeft(), pair.getRight())));
  }
}

package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.AssessmentDao;
import gov.ca.cwds.cans.dao.CaseDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.Case;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import gov.ca.cwds.cans.util.Require;
import gov.ca.cwds.security.annotations.Authorize;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/** @author denys.davydov */
public class PersonService extends AbstractCrudService<Person> {

  private final CaseDao caseDao;
  private final AssessmentDao assessmentDao;
  private final PerryService perryService;

  @Inject
  public PersonService(
      final PersonDao dao,
      final CaseDao caseDao,
      final AssessmentDao assessmentDao,
      final PerryService perryService) {
    super(dao); // NOSONAR
    this.caseDao = caseDao;
    this.assessmentDao = assessmentDao;
    this.perryService = perryService;
  }

  public Collection<Person> findAll() {
    return dao.findAll();
  }

  public Collection<Person> search(final SearchPersonPo searchPo) {
    return ((PersonDao) dao).search(searchPo);
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
    cases.forEach(aCase -> caseDao
        .createOrReplace(aCase, currentUser)
        .forEach(pair -> assessmentDao.replaceCaseIds(person.getId(), pair.getLeft(), pair.getRight()))
    );
  }
}

package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.CaseDao;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.Case;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import gov.ca.cwds.cans.util.Require;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/** @author denys.davydov */
public class PersonService extends AbstractCrudService<Person> {

  private final CaseDao caseDao;
  private final PerryService perryService;

  @Inject
  public PersonService(PersonDao dao, CaseDao caseDao, PerryService perryService) {
    super(dao); //NOSONAR
    this.caseDao = caseDao;
    this.perryService = perryService;
  }

  public Collection<Person> findAll() {
    return dao.findAll();
  }

  public Collection<Person> search(SearchPersonPo searchPo) {
    return ((PersonDao) dao).search(searchPo);
  }

  @Override
  public Person create(Person person) {
    Require.requireNotNullAndNotEmpty(person);
    initializeCasesForCreate(person.getCases());
    return super.create(person);
  }

  private void initializeCasesForCreate(List<Case> cases) {
    if (CollectionUtils.isEmpty(cases)) {
      return;
    }
    final Person currentUser = perryService.getOrPersistAndGetCurrentUser();
    for (Case aCase : cases) {
      caseDao.findByExternalIdOrCreate(aCase, currentUser);
    }
  }

  @Override
  public Person update(Person person) {
    Require.requireNotNullAndNotEmpty(person);
    initializeCasesForUpdate(person.getCases());
    return super.update(person);
  }

  private void initializeCasesForUpdate(List<Case> cases) {
    if (CollectionUtils.isEmpty(cases)) {
      return;
    }
    final Person currentUser = perryService.getOrPersistAndGetCurrentUser();
    for (Case aCase : cases) {
      caseDao.createOrReplace(aCase, currentUser);
    }
  }
}

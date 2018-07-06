package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.PersonDao;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import java.util.Collection;

/** @author denys.davydov */
public class PersonService extends AbstractCrudService<Person> {

  @Inject
  public PersonService(PersonDao dao) {
    super(dao); //NOSONAR
  }

  public Collection<Person> findAll() {
    return dao.findAll();
  }

  public Collection<Person> search(SearchPersonPo searchPo) {
    return ((PersonDao) dao).search(searchPo);
  }
}

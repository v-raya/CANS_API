package gov.ca.cwds.cans.service;

import gov.ca.cwds.cans.dao.CountyDao;
import gov.ca.cwds.cans.domain.entity.County;
import java.util.Collection;
import javax.inject.Inject;

/** @author denys.davydov */
public class CountyService {

  private final CountyDao countyDao;

  @Inject
  public CountyService(CountyDao countyDao) {
    this.countyDao = countyDao;
  }

  public Collection<County> findAll() {
    return countyDao.findAll();
  }
}

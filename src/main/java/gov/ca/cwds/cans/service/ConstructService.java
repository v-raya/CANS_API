package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.ConstructDao;
import gov.ca.cwds.cans.domain.entity.Construct;
import gov.ca.cwds.cans.util.Require;

/** @author denys.davydov */
public class ConstructService {

  private final ConstructDao constructDao;

  @Inject
  public ConstructService(ConstructDao constructDao) {
    this.constructDao = constructDao;
  }

  public Construct read(final Long id) {
    Require.requireNotNullAndNotEmpty(id);
    return constructDao.find(id);
  }

  public Construct create(final Construct construct) {
    Require.requireNotNullAndNotEmpty(construct);
    return constructDao.create(construct);
  }

  public Construct delete(final Long id) {
    Require.requireNotNullAndNotEmpty(id);
    return constructDao.delete(id);
  }
}

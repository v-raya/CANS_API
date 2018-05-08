package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.ConstructDao;
import gov.ca.cwds.cans.domain.entity.Construct;

/** @author denys.davydov */
public class ConstructService extends AbstractCrudService<Construct> {

  @Inject
  public ConstructService(ConstructDao constructDao) {
    super(constructDao);
  }
}

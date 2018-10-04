package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.InstrumentDao;
import gov.ca.cwds.cans.domain.entity.Instrument;

/** @author denys.davydov */
public class InstrumentService extends AbstractCrudService<Instrument> {

  @Inject
  public InstrumentService(InstrumentDao instrumentDao) {
    super(instrumentDao); // NOSONAR
  }
}

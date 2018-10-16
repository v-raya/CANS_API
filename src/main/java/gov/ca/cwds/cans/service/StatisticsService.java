package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.StatisticsDao;
import gov.ca.cwds.cans.domain.entity.facade.Statistics;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collection;
import java.util.Map;

/** @author denys.davydov */
public class StatisticsService {

  @Inject private StatisticsDao statisticsDao;

  @UnitOfWork(CANS)
  public Map<String, Statistics> getStaffStatistics(final Collection<String> racfIds) {
    return statisticsDao.getStaffStatistics(racfIds);
  }
}

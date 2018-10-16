package gov.ca.cwds.cans.dao;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.facade.Statistics;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.util.Require;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;

/** @author denys.davydov */
public class StatisticsDao {

  final SessionFactory sessionFactory;

  @Inject
  public StatisticsDao(@CansSessionFactory final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Map<String, Statistics> getStaffStatistics(final Collection<String> racfIds) {
    Require.requireNotNullAndNotEmpty(racfIds);
    final List<Statistics> statisticsList =
        this.sessionFactory
            .getCurrentSession()
            .createNamedQuery(Statistics.NQ_STAFF_ASSESSMENT_STATISTICS, Statistics.class)
            .setParameter(Statistics.NQ_PARAM_RACF_IDS, racfIds)
            .list();
    final Map<String, Statistics> result =
        statisticsList.stream().collect(Collectors.toMap(Statistics::getExternalId, stat -> stat));
    // fill default empty value for not found ids
    racfIds
        .stream()
        .filter(id -> result.get(id) == null)
        .forEach(id -> result.put(id, new Statistics().setExternalId(id)));
    return result;
  }
}

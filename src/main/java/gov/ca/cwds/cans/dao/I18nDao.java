package gov.ca.cwds.cans.dao;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.I18n;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import java.util.Collection;
import java.util.List;
import org.hibernate.SessionFactory;

/**
 * @author denys.davydov
 */
public class I18nDao extends AbstractCrudDao<Person> {

  @Inject
  public I18nDao(@CansSessionFactory final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Collection<I18n> findByKeyPrefixAndLanguage(String keyPrefix, String lang) {
    final List<I18n> entities = this.getSessionFactory().getCurrentSession()
        .createNamedQuery(I18n.NQ_FIND_BY_KEY_PREFIX_AND_LANG, I18n.class)
        .setParameter(I18n.NQ_PARAM_KEY_PREFIX, keyPrefix + "%")
        .setParameter(I18n.NQ_PARAM_LANG, lang)
        .list();

    return ImmutableList.copyOf(entities);
  }
}

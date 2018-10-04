package gov.ca.cwds.cans.service;

import com.google.inject.Inject;
import gov.ca.cwds.cans.dao.I18nDao;
import gov.ca.cwds.cans.domain.entity.I18n;
import gov.ca.cwds.cans.util.Require;
import java.util.Collection;

/** @author denys.davydov */
public class I18nService {

  private final I18nDao i18nDao;

  @Inject
  public I18nService(I18nDao i18nDao) {
    this.i18nDao = i18nDao;
  }

  public Collection<I18n> findByKeyPrefixAndLanguage(String keyPrefix, String lang) {
    Require.requireNotNullAndNotEmpty(keyPrefix);
    Require.requireNotNullAndNotEmpty(lang);
    return i18nDao.findByKeyPrefixAndLanguage(keyPrefix, lang);
  }
}

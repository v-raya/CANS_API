package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.entity.I18n;
import gov.ca.cwds.cans.util.Require;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface I18nMapper {
  default Map<String, String> toMap(Collection<I18n> entities) {
    final Map<String, String> results = new LinkedHashMap<>();
    if (CollectionUtils.isEmpty(entities)) {
      return results;
    }

    entities.forEach(i18n -> results.put(i18n.getK(), i18n.getV()));
    return results;
  }

  default Map<String, String> toMapWithKeyPrefixCut(Collection<I18n> entities, String keyPrefix) {
    final Map<String, String> results = new LinkedHashMap<>();
    if (CollectionUtils.isEmpty(entities)) {
      return results;
    }

    Require.requireNotNullAndNotEmpty(keyPrefix);
    final int keyPrefixLength = keyPrefix.length();
    entities.forEach(i18n -> results.put(i18n.getK().substring(keyPrefixLength), i18n.getV()));
    return results;
  }
}

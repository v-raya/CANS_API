package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.entity.I18n;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface I18nMapper {
  default Map<String, String> toMap(Collection<I18n> entities) {
    final Map<String, String> results = new HashMap<>();
    if (CollectionUtils.isEmpty(entities)) {
      return results;
    }

    entities.forEach(i18n -> results.put(i18n.getK(), i18n.getV()));
    return results;
  }
}

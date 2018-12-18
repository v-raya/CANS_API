package gov.ca.cwds.cans.domain.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
public abstract class Dto {

  Long id;
  Map<String, Object> metadata;

  public void addMetadata(String key, Object value) {
    if (metadata == null) {
      metadata = new HashMap<>();
    }
    metadata.put(key, value);
  }
}

package gov.ca.cwds.cans.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
public abstract class Dto {
  Long id;
}

package gov.ca.cwds.cans.domain.entity;

import java.io.Serializable;

/** @author denys.davydov */
public interface Persistent<I extends Serializable> {
  I getId();

  Persistent setId(I id);
}

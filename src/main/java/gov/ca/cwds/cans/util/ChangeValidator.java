package gov.ca.cwds.cans.util;

import gov.ca.cwds.cans.util.ChangesBuilder.BuilderError;
import liquibase.change.Change;

/** @author CWDS TPT-2 Team */
@FunctionalInterface
public interface ChangeValidator<T extends Change> {
  BuilderError validate(T change);
}

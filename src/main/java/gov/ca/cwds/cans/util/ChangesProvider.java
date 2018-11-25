package gov.ca.cwds.cans.util;

import java.util.List;
import liquibase.change.Change;

/** @author CWDS TPT-2 Team */
public interface ChangesProvider {
  List<Change> getChanges();
}

package gov.ca.cwds.cans.util;

import java.sql.Connection;
import java.sql.SQLException;

/** @author CWDS TPT-2 Team */
public interface ConnectionProvider {
  Connection getConnection() throws SQLException;
}

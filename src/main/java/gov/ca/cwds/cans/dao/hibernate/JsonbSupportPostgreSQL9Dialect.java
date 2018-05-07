package gov.ca.cwds.cans.dao.hibernate;

import org.hibernate.dialect.PostgreSQL9Dialect;

import java.sql.Types;

/**
 * @author CWDS CALS API Team
 */
public class JsonbSupportPostgreSQL9Dialect extends PostgreSQL9Dialect {

  public JsonbSupportPostgreSQL9Dialect() {
    super();
    this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
  }
}

package gov.ca.cwds.cans.dao.hibernate;

import java.sql.Types;
import org.hibernate.dialect.PostgreSQL9Dialect;

/** @author CWDS CALS API Team */
public class JsonbSupportPostgreSQL9Dialect extends PostgreSQL9Dialect {

  public JsonbSupportPostgreSQL9Dialect() {
    super();
    this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
  }
}

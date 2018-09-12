package gov.ca.cwds.cans.dao.hibernate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class PostgreSqlEnumType extends org.hibernate.type.EnumType {

  @Override
  public void nullSafeSet(PreparedStatement statement, Object value, int index,
      SharedSessionContractImplementor session) throws SQLException {
    if (value == null) {
      statement.setNull(index, Types.OTHER);
    } else {
      statement.setObject(index, value.toString(), Types.OTHER);
    }
  }
}

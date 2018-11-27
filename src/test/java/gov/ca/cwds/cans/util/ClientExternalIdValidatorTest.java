package gov.ca.cwds.cans.util;

import static gov.ca.cwds.cans.util.DbUpgradeConstants.CLIENT_EXTERNAL_ID_FIELD_NAME;
import static gov.ca.cwds.cans.util.DbUpgradeConstants.CLIENT_TABLE_NAME;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import gov.ca.cwds.cans.util.ChangesBuilder.BuilderError;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import liquibase.change.ColumnConfig;
import liquibase.change.core.UpdateDataChange;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** @author CWDS TPT-2 Team */
public class ClientExternalIdValidatorTest {

  @Mock private Connection connection;

  @Mock private PreparedStatement preparedStatement;

  @Mock private ResultSet resultSet;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void validate_ValidChange_success() throws Exception {
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);
    ClientExternalIdValidator validator = new ClientExternalIdValidator(() -> connection);
    UpdateDataChange updateDataChange = new UpdateDataChange();
    updateDataChange.setTableName(CLIENT_TABLE_NAME);
    LinkedList<ColumnConfig> colunmns = new LinkedList<>();
    ColumnConfig columnConfig = new ColumnConfig();
    columnConfig.setName(CLIENT_EXTERNAL_ID_FIELD_NAME);
    columnConfig.setValue("new_value");
    colunmns.add(columnConfig);
    updateDataChange.setColumns(colunmns);

    Assert.assertThat(validator.validate(updateDataChange), IsNull.nullValue());
  }

  @Test
  public void validate_InvalidChange_validationError() throws Exception {
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true);
    ClientExternalIdValidator validator = new ClientExternalIdValidator(() -> connection);
    UpdateDataChange updateDataChange = new UpdateDataChange();
    updateDataChange.setTableName(CLIENT_TABLE_NAME);
    LinkedList<ColumnConfig> colunmns = new LinkedList<>();
    ColumnConfig columnConfig = new ColumnConfig();
    columnConfig.setName(CLIENT_EXTERNAL_ID_FIELD_NAME);
    columnConfig.setValue("new_value");
    colunmns.add(columnConfig);
    updateDataChange.setColumns(colunmns);

    BuilderError error = validator.validate(updateDataChange);
    Assert.assertNotNull(error);
  }

  @Test
  public void validate_noChangeForClientTable_success() throws Exception {
    ClientExternalIdValidator validator = new ClientExternalIdValidator(() -> connection);
    UpdateDataChange updateDataChange = new UpdateDataChange();
    updateDataChange.setTableName("some_other_table");
    Assert.assertThat(validator.validate(updateDataChange), IsNull.nullValue());
  }
}

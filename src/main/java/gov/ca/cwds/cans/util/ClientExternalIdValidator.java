package gov.ca.cwds.cans.util;

import static gov.ca.cwds.cans.util.DbUpgradeConstants.CLIENT_EXTERNAL_ID_FIELD_NAME;
import static gov.ca.cwds.cans.util.DbUpgradeConstants.CLIENT_TABLE_NAME;

import gov.ca.cwds.cans.util.ChangesBuilder.BuilderError;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import liquibase.change.Change;
import liquibase.change.ColumnConfig;
import liquibase.change.core.UpdateDataChange;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CWDS TPT-2 Team
 */
@Slf4j
public class ClientExternalIdValidator implements ChangeValidator {

  private ConnectionProvider connectionProvider;

  public ClientExternalIdValidator(ConnectionProvider connectionProvider) {
    this.connectionProvider = connectionProvider;
  }

  @Override
  public BuilderError validate(Change change) {
    String valueForUpdate = extractValueForUpdate(change);
    if (valueForUpdate == null) {
      return null;
    }
    BuilderError error = null;
    try (Connection conn = connectionProvider.getConnection()) {
      try (PreparedStatement statement = conn
          .prepareStatement("SELECT external_id FROM person WHERE external_id=?")) {
        statement.setString(1, valueForUpdate);
        if (statement.executeQuery().next()) {
          String uiIdentifier = CmsKeyIdGenerator.getUIIdentifierFromKey(valueForUpdate);
          error =
              new BuilderError(
                  "person.external_id ["
                      + valueForUpdate
                      + "] already exist,"
                      + " current person.external_id ["
                      + uiIdentifier
                      + "] can't be updated",
                  null);
        }
      }
    } catch (Exception e) {
      throw new UpgradeDbException(e.getMessage(), e);
    }
    return error;
  }

  private String extractValueForUpdate(Change change) {
    String valueForUpdate = null;
    if (change instanceof UpdateDataChange
        && ((UpdateDataChange) change).getTableName().equalsIgnoreCase(CLIENT_TABLE_NAME)) {
      UpdateDataChange updateDataChange = (UpdateDataChange) change;
      valueForUpdate =
          updateDataChange
              .getColumns()
              .stream()
              .filter(
                  columnConfig ->
                      columnConfig.getName().equalsIgnoreCase(CLIENT_EXTERNAL_ID_FIELD_NAME))
              .findFirst()
              .map(ColumnConfig::getValue)
              .orElse(null);
    }
    return valueForUpdate;
  }
}

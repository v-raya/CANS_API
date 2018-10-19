package gov.ca.cwds.cans;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.rest.MinimalApiConfiguration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true) // NOSONAR
@Data
public class CansConfiguration extends MinimalApiConfiguration {

  @JsonProperty private DataSourceFactory cansDataSourceFactory;
  @JsonProperty private DataSourceFactory cmsDataSourceFactory;
  @JsonProperty private DataSourceFactory cmsRsDataSourceFactory;

  private Boolean upgradeCansDbOnStart;
  private Boolean populateDemoDataOnStart;
}

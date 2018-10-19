package gov.ca.cwds.cans.rest.resource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.cans.Constants;
import gov.ca.cwds.dto.app.HealthCheckResultDto;
import gov.ca.cwds.dto.app.SystemInformationDto;
import java.util.SortedMap;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/** Created by denys.davydov */
public class SystemInformationResourceTest extends AbstractFunctionalTest {

  @Test
  public void getSystemInformation_success() {
    final SystemInformationDto systemInformation =
        clientTestRule
            .target(Constants.API.SYSTEM_INFORMATION)
            .request(MediaType.APPLICATION_JSON)
            .get(SystemInformationDto.class);

    assertThat(systemInformation.getApplicationName(), is(equalTo("CWDS CANS API")));
    assertThat(systemInformation.getVersion(), is(notNullValue()));

    SortedMap<String, HealthCheckResultDto> healthCheckResults =
        systemInformation.getHealthCheckResults();
    assertHealthCheck(healthCheckResults.get("deadlocks"));
    assertHealthCheck(healthCheckResults.get(Constants.UnitOfWork.CANS));
    assertHealthCheck(healthCheckResults.get(Constants.UnitOfWork.CMS));
  }

  private void assertHealthCheck(final HealthCheckResultDto healthCheckResult) {
    assertThat(healthCheckResult, is(notNullValue()));
    assertThat(healthCheckResult.isHealthy(), is(equalTo(true)));
  }
}

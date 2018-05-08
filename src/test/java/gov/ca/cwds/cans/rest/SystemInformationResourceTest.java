package gov.ca.cwds.cans.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.cans.Constants;
import gov.ca.cwds.cans.domain.dto.system.HealthCheckResultDto;
import gov.ca.cwds.cans.domain.dto.system.SystemInformationDto;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/**
 * Created by denys.davydov
 */
public class SystemInformationResourceTest extends AbstractIntegrationTest {

  @Test
  public void testSystemInformationGet() {
    final SystemInformationDto systemInformation = clientTestRule
        .target(Constants.API.SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON)
        .get(SystemInformationDto.class);

    assertThat(systemInformation.getApplication(), is(equalTo("CWDS CANS API")));
    assertThat(systemInformation.getVersion(), is(notNullValue()));

    assertHealthCheck(systemInformation.getDeadlocks());
//    assertHealthCheck(systemInformation.getCans());
  }

  private void assertHealthCheck(final HealthCheckResultDto healthCheckResult) {
    assertThat(healthCheckResult, is(notNullValue()));
    assertThat(healthCheckResult.isHealthy(), is(equalTo(true)));
  }

}

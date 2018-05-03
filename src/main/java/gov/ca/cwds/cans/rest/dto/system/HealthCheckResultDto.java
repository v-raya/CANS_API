package gov.ca.cwds.cans.rest.dto.system;

import com.codahale.metrics.health.HealthCheck;
import gov.ca.cwds.cans.rest.dto.AbstractBaseDto;
import java.util.HashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author CWDS CANS API Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("squid:S2160")
public class HealthCheckResultDto extends AbstractBaseDto {

  private static final long serialVersionUID = 6340795706320750307L;

  private boolean healthy;
  private String message;
  private Throwable error;
  private HashMap<String, Object> details;
  private String timestamp;

  public void setResult(HealthCheck.Result result) {
    setHealthy(result.isHealthy());
    setMessage(result.getMessage());
    setError(result.getError());
    setDetails((HashMap<String, Object>) result.getDetails());
    setTimestamp(result.getTimestamp());
  }
}

package gov.ca.cwds.cans.domain.dto.system;

import com.codahale.metrics.health.HealthCheck;
import java.util.HashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author CWDS CANS API Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HealthCheckResultDto {

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

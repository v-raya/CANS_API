package gov.ca.cwds.cans.domain.dto.system;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.HashMap;
import lombok.Data;
import lombok.experimental.Accessors;

/** @author CWDS CANS API Team */
@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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

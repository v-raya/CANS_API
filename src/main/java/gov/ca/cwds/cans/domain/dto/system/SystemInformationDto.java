package gov.ca.cwds.cans.domain.dto.system;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * @author CWDS CANS API Team
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SystemInformationDto {
  private String application;
  private String version;
  private String buildNumber;
  private HealthCheckResultDto cans;
  private HealthCheckResultDto deadlocks;
}

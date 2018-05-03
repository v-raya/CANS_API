package gov.ca.cwds.cans.rest.dto.system;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.rest.dto.AbstractBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author CWDS CANS API Team
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("squid:S2160")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SystemInformationDto extends AbstractBaseDto {

  private static final long serialVersionUID = 2548070376186176867L;

  private String application;
  private String version;
  private String buildNumber;
  private HealthCheckResultDto cans;
  private HealthCheckResultDto deadlocks;
}

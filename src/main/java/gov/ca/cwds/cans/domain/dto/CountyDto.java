package gov.ca.cwds.cans.domain.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * @author denys.davydov
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CountyDto {
  private Long id;
  private String name;
  private String externalId;
  private String exportId;
}

package gov.ca.cwds.cans.domain.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StaffPersonDto extends Dto {
  private String identifier;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String phoneExtCode;
  private String email;
  private CountyDto county;
}

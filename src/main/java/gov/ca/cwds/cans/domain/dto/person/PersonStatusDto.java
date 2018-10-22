package gov.ca.cwds.cans.domain.dto.person;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonStatusDto extends Dto {
  private String externalId;
  private AssessmentStatus status;
}

package gov.ca.cwds.cans.domain.dto.person;

import static gov.ca.cwds.rest.api.domain.DomainObject.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.enumeration.ClientAssessmentStatus;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
public class StaffClientDto extends PersonShortDto {

  public StaffClientDto(Long id, String externalId, String status, LocalDate eventDate) {
    this.setId(id);
    this.setExternalId(externalId);
    if (eventDate != null) {
      this.reminderDate = eventDate.plusMonths(6);
    }
    this.status = ClientAssessmentStatus.valueOf(status);
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate reminderDate;

  private ClientAssessmentStatus status;
}

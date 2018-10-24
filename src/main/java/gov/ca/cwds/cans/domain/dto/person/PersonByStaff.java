package gov.ca.cwds.cans.domain.dto.person;

import gov.ca.cwds.data.legacy.cms.entity.facade.ClientByStaff;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PersonByStaff extends ClientByStaff {
  public static final int REMINDER_DATE_PERIOD_MONTHS = 6;

  private PersonStatusDto personStatus;
  private LocalDate reminderDate;

  public PersonByStaff(ClientByStaff clientByStaff) {
    super(
        clientByStaff.getIdentifier(),
        clientByStaff.getFirstName(),
        clientByStaff.getMiddleName(),
        clientByStaff.getLastName(),
        clientByStaff.getNameSuffix(),
        clientByStaff.getSensitivityType(),
        clientByStaff.getBirthDate(),
        clientByStaff.getCasePlanReviewDueDate());
  }
}

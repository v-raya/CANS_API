package gov.ca.cwds.cans.domain.dto.facade;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.dto.StaffPersonDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/** @author denys.davydov */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StaffStatisticsDto extends Dto {
  private StaffPersonDto staffPerson;
  private int clientsCount;
  private int noPriorCansCount;
  private int inProgressCount;
  private int completedCount;

  public void incrementNoPriorCansCount() {
    noPriorCansCount++;
  }

  public void incrementInProgressCount() {
    inProgressCount++;
  }

  public void incrementCompletedCount() {
    completedCount++;
  }
}

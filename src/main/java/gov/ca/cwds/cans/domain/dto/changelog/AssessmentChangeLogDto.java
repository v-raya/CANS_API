package gov.ca.cwds.cans.domain.dto.changelog;

import static gov.ca.cwds.rest.api.domain.DomainObject.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.enumeration.AssessmentChangeType;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import java.time.LocalDate;
import java.util.Collections;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.envers.RevisionType;

/**
 * Assessment chnage log dto
 *
 * @author CWDS API Team
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AssessmentChangeLogDto extends AbstractChangeLogDto<Assessment> {

  @JsonIgnore AssessmentStatus assessmentStatus;
  AssessmentChangeType assessmentChangeType;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  LocalDate eventDate;

  public AssessmentChangeLogDto() {
    // default constructor
  }

  AssessmentChangeLogDto(ChangeLogDtoParameters<Assessment> dtoParams) {
    super(dtoParams);
    assessmentStatus = dtoParams.getCurrent().getStatus();
    eventDate = dtoParams.getCurrent().getEventDate();
    assessmentChangeType = fromRevisionTypeAndStatus();
  }

  @Override
  void populateChanges(Assessment current, Assessment previous) {
    // Do the Diff and populate changes
    setChanges(Collections.emptyList());
  }

  private AssessmentChangeType fromRevisionTypeAndStatus() {
    // Supports soft/hard delete
    boolean isDeleted =
        AssessmentStatus.DELETED.equals(assessmentStatus)
            || RevisionType.DEL.equals(getChangeType());

    boolean isCompleted = AssessmentStatus.COMPLETED.equals(assessmentStatus);

    boolean isCreated =
        AssessmentStatus.IN_PROGRESS.equals(assessmentStatus)
            && RevisionType.ADD.equals(getChangeType());
    return getAssessmentChangeType(isDeleted, isCompleted, isCreated);
  }

  private AssessmentChangeType getAssessmentChangeType(
      boolean isDeleted, boolean isCompleted, boolean isCreated) {
    AssessmentChangeType ret;
    if (isDeleted) {
      ret = AssessmentChangeType.DELETED;
    } else if (isCompleted) {
      ret = AssessmentChangeType.COMPLETED;
    } else if (isCreated) {
      ret = AssessmentChangeType.CREATED;
    } else {
      ret = AssessmentChangeType.SAVED;
    }
    return ret;
  }
}

package gov.ca.cwds.cans.domain.dto.changelog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.enumeration.AssessmentChangeType;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
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

  public AssessmentChangeLogDto() {
    // default constructor
  }

  AssessmentChangeLogDto(ChangeLogDtoParameters<Assessment> dtoParams) {
    super(dtoParams);
    assessmentStatus = dtoParams.getCurrent().getStatus();
    assessmentChangeType = fromRevisionTypeAndStatus();
  }

  @Override
  void populateChanges(Assessment current, Assessment previous) {
    // Do the Diff and populate changes
    setChanges(Collections.emptyList());
  }

  private AssessmentChangeType fromRevisionTypeAndStatus() {
    // Supports soft/hard delete
    AssessmentChangeType ret;
    if (RevisionType.DEL.equals(getChangeType())) {
      ret = AssessmentChangeType.DELETED;
    } else {
      switch (assessmentStatus) {
        case DELETED:
          ret = AssessmentChangeType.DELETED;
          break;

        case COMPLETED:
          ret = AssessmentChangeType.COMPLETED;
          break;

        case IN_PROGRESS:
        default:
          if (RevisionType.ADD.equals(getChangeType())) {
            ret = AssessmentChangeType.CREATED;
          } else {
            ret = AssessmentChangeType.SAVED;
          }
          break;
      }
    }
    return ret;
  }
}

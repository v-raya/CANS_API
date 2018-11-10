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
    assessmentChangeType = fromRevisionTypeAndStatus(dtoParams.getRevisionType());
  }

  @Override
  void populateChanges(Assessment current, Assessment previous) {
    // Do the Diff and populate changes
    setChanges(Collections.emptyList());
  }

  private AssessmentChangeType fromRevisionTypeAndStatus(final RevisionType revisionType) {
    AssessmentChangeType ret;
    if (revisionType == null || assessmentStatus == null) {
      ret = null;
    }
    // Supports soft/hard delete
    else if (AssessmentStatus.DELETED.equals(assessmentStatus)
        || RevisionType.DEL.equals(revisionType)) {
      ret = AssessmentChangeType.DELETED;
    } else if (AssessmentStatus.COMPLETED.equals(assessmentStatus)) {
      ret = AssessmentChangeType.COMPLETED;
    } else if (AssessmentStatus.IN_PROGRESS.equals(assessmentStatus)
        && RevisionType.ADD.equals(revisionType)) {
      ret = AssessmentChangeType.CREATED;
    } else {
      ret = AssessmentChangeType.SAVED;
    }
    return ret;
  }
}

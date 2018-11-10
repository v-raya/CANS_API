package gov.ca.cwds.cans.domain.dto.changelog;

import static gov.ca.cwds.rest.api.domain.DomainObject.TIMESTAMP_ISO8601_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.entity.Persistent;
import gov.ca.cwds.cans.domain.entity.envers.NsRevisionEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.envers.RevisionType;

/**
 * Abstract change log dto
 *
 * @author CWDS API Team
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractChangeLogDto<E extends Persistent> extends Dto {

  private String userId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIMESTAMP_ISO8601_FORMAT)
  private LocalDateTime changedAt;
  private RevisionType changeType;
  private List<Change> changes;

  private AbstractChangeLogDto() {
  }

  AbstractChangeLogDto(NsRevisionEntity revisionEntity, RevisionType revisionType, E current,
      E previous) {
    setId(revisionEntity.getId());
    userId = revisionEntity.getUserId();
    changedAt = revisionEntity.getRevisionDate();
    changeType = revisionType;
    populateChanges(current, previous);
  }

  abstract void populateChanges(E current, E previous);

  @Data
  @Accessors(chain = true)
  @EqualsAndHashCode()
  public class Change {

    String elementName;
    String beofreValue;
    String afterValue;
  }
}

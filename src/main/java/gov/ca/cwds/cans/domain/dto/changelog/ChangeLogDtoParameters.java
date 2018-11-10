package gov.ca.cwds.cans.domain.dto.changelog;

import gov.ca.cwds.cans.domain.entity.Persistent;
import gov.ca.cwds.cans.domain.entity.envers.NsRevisionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.envers.RevisionType;

/**
 * Wrapper for change log dto constructor parameters
 *
 * @author CWDS API Team
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class ChangeLogDtoParameters<E extends Persistent> {

  private NsRevisionEntity revisionEntity = null;
  private RevisionType revisionType = null;
  private E current = null;
  private E previous = null;

}

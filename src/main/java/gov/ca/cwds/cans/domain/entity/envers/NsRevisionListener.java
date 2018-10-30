package gov.ca.cwds.cans.domain.entity.envers;

import gov.ca.cwds.cans.rest.filters.RequestExecutionContext;
import org.hibernate.envers.RevisionListener;

/**
 * NS revision listener. Sets the user_id from request
 *
 * @author CWDS API Team
 */
public class NsRevisionListener implements RevisionListener {

  public void newRevision(Object revisionEntity) {
    ((NsRevisionEntity) revisionEntity).setUserId(RequestExecutionContext.instance().getUserId());
  }
}

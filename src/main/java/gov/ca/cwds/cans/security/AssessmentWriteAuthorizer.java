package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.data.legacy.cms.entity.enums.AccessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssessmentWriteAuthorizer extends BaseAssessmentAuthorizer {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentWriteAuthorizer.class);

  @Inject private ClientReadAuthorizer clientReadAuthorizer;

  @Override
  protected boolean checkInstance(Assessment assessment) {
    String clientId = assessment.getPerson().getExternalId();
    return clientReadAuthorizer.checkSealedSensitive(clientId)
        || checkByAssignment(clientId)
        || checkBySubordinateAssignment(clientId);
  }

  @Override
  protected Long stringToId(String id) {
    return Long.valueOf(id);
  }

  private boolean checkByAssignment(String clientId) {
    boolean isAssignedToClient = clientReadAuthorizer.getAccessType(clientId) == AccessType.RW;
    LOG.info(
        "Authorization: client [{}] assigned with RW check result [{}]",
        clientId,
        isAssignedToClient);
    return isAssignedToClient;
  }

  private boolean checkBySubordinateAssignment(String clientId) {
    boolean isAssignedToSubordinate =
        clientReadAuthorizer.getAccessTypeBySupervisor(clientId) == AccessType.RW;
    LOG.info(
        "Authorization: client [{}] subordinates assignment with RW check result [{}]",
        clientId,
        isAssignedToSubordinate);
    return isAssignedToSubordinate;
  }
}

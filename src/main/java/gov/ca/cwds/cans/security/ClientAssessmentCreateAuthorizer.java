package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.mapper.ClientMapper;
import gov.ca.cwds.cans.security.assessment.AssessmentCreateAuthorizer;
import gov.ca.cwds.cans.service.ClientsService;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;

/**
 * @author CWDS TPT-2 Team
 *     <p>This check must work through the
 *     gov.ca.cwds.cans.security.assessment.AssessmentCreateAuthorizer
 */
@Deprecated
public class ClientAssessmentCreateAuthorizer extends BaseAuthorizer<Client, String> {

  @Inject private AssessmentCreateAuthorizer assessmentCreateAuthorizer;
  @Inject private ClientsService clientsService;
  @Inject private ClientMapper clientMapper;

  @Override
  protected boolean checkId(String clientId) {
    Assessment assessment = new Assessment();
    ClientDto client = clientsService.findByExternalId(clientId);
    Person person = clientMapper.toPerson(client);
    assessment.setPerson(person);
    return assessmentCreateAuthorizer.check(assessment);
  }
}

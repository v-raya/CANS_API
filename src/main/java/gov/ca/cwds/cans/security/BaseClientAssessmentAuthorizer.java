package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.mapper.ClientMapper;
import gov.ca.cwds.cans.domain.mapper.CountyMapper;
import gov.ca.cwds.cans.service.ClientsService;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.security.authorizer.Authorizer;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import java.util.List;

/** @author CWDS TPT-2 Team */
public abstract class BaseClientAssessmentAuthorizer extends BaseAuthorizer<Client, String> {

  private Authorizer authorizer;
  @Inject private ClientsService clientsService;
  @Inject private ClientMapper clientMapper;
  @Inject private CountyMapper countyMapper;

  public BaseClientAssessmentAuthorizer(Authorizer authorizer) {
    this.authorizer = authorizer;
  }

  @Override
  protected boolean checkId(String clientId) {
    Assessment assessment = new Assessment();
    List<CountyDto> countyDtos = clientsService.getCountyDtos(clientId);
    CountyDto countyDto = clientMapper.toCansCounty(countyDtos);
    County county = countyMapper.fromDto(countyDto);
    Person person = new Person();
    person.setCounty(county);
    assessment.setPerson(person);
    return authorizer.check(assessment);
  }

  protected boolean checkInstance(Client client) {
    return checkId(client.getIdentifier());
  }
}

package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS_RS;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.CaseDto;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.person.ChildDto;
import gov.ca.cwds.cans.domain.mapper.ChildMapper;
import gov.ca.cwds.cans.domain.mapper.CountyMapper;
import gov.ca.cwds.data.legacy.cms.dao.CaseDao;
import gov.ca.cwds.data.legacy.cms.dao.ChildClientDao;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.service.ClientCountyDeterminationService;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author CWDS TPT-2 Team
 */
public class ChildrenService {


  @Inject
  private ChildClientDao childClientDao;
  @Inject
  private ChildMapper childMapper;
  @Inject
  private ClientCountyDeterminationService countyDeterminationService;

  @Inject
  private CountyService countyService;
  @Inject
  private CountyMapper countyMapper;

  @Inject
  private CaseDao cmsCaseDao;

  private static Map<String, CountyDto> countiesCache = new HashMap<>();

  public ChildDto findByExternalId(String id) {
    return Optional.ofNullable(findClient(id)).map(this::composeChildDto).orElse(null);
  }

  private ChildDto composeChildDto(Client client) {
    String clientId = client.getIdentifier();
    return childMapper.toChildDto(client, getCountyDtos(clientId), findClientCases(clientId));
  }

  private List<CountyDto> getCountyDtos(String clientId) {
    Collection<Short> countyIds = determineClientCounties(clientId);

    return countyIds.stream().map(countyExternalId ->
        findCountyDto(String.valueOf(countyExternalId))
    ).collect(Collectors.toList());
  }

  @UnitOfWork(CMS)
  protected List<CaseDto> findClientCases(String clientId) {
    return Optional.ofNullable(cmsCaseDao.findActiveByClient(clientId))
        .map(cmsCases -> childMapper.toCaseDtoList(cmsCases))
        .orElse(Collections.emptyList());
  }

  @UnitOfWork(CMS)
  protected Client findClient(String id) {
    Client client = childClientDao.find(id);
    if (client != null && !client.getChildClientIndicator()) {
      throw new IllegalArgumentException("The client with ID: " + id + " is not a childClient");
    }
    return client;
  }

  @UnitOfWork(CMS_RS)
  protected Collection<Short> determineClientCounties(String cmsClientId) {
    return Optional.ofNullable(countyDeterminationService.getClientCountiesById(cmsClientId))
        .orElse(Collections.emptyList());
  }

  @UnitOfWork(CANS)
  protected CountyDto findCountyDto(String externalId) {
    if (countiesCache.isEmpty()) {
      Collection<CountyDto> countyDtos = countyMapper.toDtos(countyService.findAll());
      countyDtos.forEach(countyDto -> countiesCache.put(countyDto.getExternalId(), countyDto));
    }
    return countiesCache.get(externalId);
  }

}

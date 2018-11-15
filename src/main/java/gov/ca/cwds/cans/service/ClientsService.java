package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS_RS;

import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.enumeration.ServiceSource;
import gov.ca.cwds.cans.domain.mapper.ClientMapper;
import gov.ca.cwds.cans.domain.mapper.CountyMapper;
import gov.ca.cwds.data.legacy.cms.dao.CaseDao;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.dao.ReferralDao;
import gov.ca.cwds.data.legacy.cms.entity.Case;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import gov.ca.cwds.service.ClientCountyDeterminationService;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.shiro.SecurityUtils;

/** @author CWDS TPT-2 Team */
@SuppressFBWarnings("PMB_POSSIBLE_MEMORY_BLOAT")
public class ClientsService {

  @Inject private ClientDao clientDao;
  @Inject private ClientMapper clientMapper;
  @Inject private ClientCountyDeterminationService countyDeterminationService;

  @Inject private CountyService countyService;
  @Inject private CountyMapper countyMapper;

  @Inject private CaseDao cmsCaseDao;
  @Inject private ReferralDao cmsReferralDao;

  private static Map<String, CountyDto> countiesCache = new HashMap<>(); // NOSONAR

  public ClientDto findByExternalId(String id) {
    SecurityUtils.getSubject().checkPermission("client:read:" + id);
    return Optional.ofNullable(findClient(id)).map(this::composeClientDto).orElse(null);
  }

  private ClientDto composeClientDto(Client client) {
    final List<CountyDto> counties = getCountyDtos(client.getIdentifier());
    final ClientDto result = clientMapper.toClientDto(client, counties);
    enhanceWithCaseOrReferralId(result);
    return result;
  }

  private void enhanceWithCaseOrReferralId(ClientDto clientDto) {
    final String clientId = clientDto.getIdentifier();
    final List<Case> clientCases = findClientCases(clientId);
    if (!clientCases.isEmpty()) {
      enhanceWithCaseOrReferralId(
          clientDto, clientCases.get(0).getIdentifier(), ServiceSource.CASE);
    } else {
      final List<String> referralIds = findClientReferrals(clientId);
      if (!referralIds.isEmpty()) {
        enhanceWithCaseOrReferralId(clientDto, referralIds.get(0), ServiceSource.REFERRAL);
      }
    }
  }

  private void enhanceWithCaseOrReferralId(
      ClientDto clientDto, String caseOrReferralId, ServiceSource serviceSource) {
    clientDto.setServiceSourceId(caseOrReferralId);
    clientDto.setServiceSourceUiId(CmsKeyIdGenerator.getUIIdentifierFromKey(caseOrReferralId));
    clientDto.setServiceSource(serviceSource);
  }

  private List<CountyDto> getCountyDtos(String clientId) {
    Collection<Short> countyIds = determineClientCounties(clientId);

    return countyIds
        .stream()
        .map(countyExternalId -> findCountyDto(String.valueOf(countyExternalId)))
        .collect(Collectors.toList());
  }

  @UnitOfWork(CMS)
  protected List<Case> findClientCases(String clientId) {
    return cmsCaseDao.findActiveByClient(clientId);
  }

  @UnitOfWork(CMS)
  protected List<String> findClientReferrals(String clientId) {
    return cmsReferralDao.findReferralIdsByClientIdAndActiveDate(clientId, LocalDate.now());
  }

  @UnitOfWork(CMS)
  protected Client findClient(String id) {
    return clientDao.find(id);
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

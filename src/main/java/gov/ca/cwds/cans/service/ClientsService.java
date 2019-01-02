package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.cans.cache.Cached;
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
import gov.ca.cwds.data.legacy.cms.entity.facade.ClientCounty;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import gov.ca.cwds.security.annotations.Authorize;
import gov.ca.cwds.service.ClientCountyDeterminationService;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author CWDS TPT-2 Team */
@SuppressFBWarnings("PMB_POSSIBLE_MEMORY_BLOAT")
public class ClientsService {

  private final Logger LOG = LoggerFactory.getLogger(ClientsService.class);

  private static Map<String, CountyDto> countiesCache = new HashMap<>(); // NOSONAR
  @Inject private ClientDao clientDao;
  @Inject private ClientMapper clientMapper;
  @Inject private ClientCountyDeterminationService countyDeterminationService;
  @Inject private CountyService countyService;
  @Inject private CountyMapper countyMapper;
  @Inject private CaseDao cmsCaseDao;
  @Inject private ReferralDao cmsReferralDao;

  @Cached
  public ClientDto findByExternalId(String id) {
    return findByExternalIdSecured(id);
  }

  ClientDto findByExternalIdSecured(@Authorize("client:read:id") String id) {
    return Optional.ofNullable(findClient(id)).map(this::composeClientDto).orElse(null);
  }

  private ClientDto composeClientDto(Client client) {
    final CountyDto county = getCountyDto(client.getIdentifier());
    final ClientDto result = clientMapper.toClientDto(client, county);
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

  @Cached
  public CountyDto getCountyDto(String clientId) {
    Short countyId = determineClientCounty(clientId);
    return findCountyDto(String.valueOf(countyId));
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

  @UnitOfWork(CMS)
  protected Short determineClientCounty(String cmsClientId) {
    ClientCounty clientCounty =
        countyDeterminationService.getClientCountiesRealtimeById(cmsClientId);
    LOG.info("Authorization: client [{}] county determined [{}]", cmsClientId, clientCounty);
    return clientCounty.getCountyCode().shortValue();
  }

  private CountyDto findCountyDto(String externalId) {
    if (countiesCache.isEmpty()) {
      Collection<CountyDto> countyDtos = countyMapper.toDtos(countyService.findAll());
      countyDtos.forEach(countyDto -> countiesCache.put(countyDto.getExternalId(), countyDto));
    }
    return countiesCache.get(externalId);
  }
}

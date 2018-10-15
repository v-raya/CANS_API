package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.person.ChildDto;
import gov.ca.cwds.cans.domain.mapper.ChildMapper;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * @author CWDS TPT-2 Team
 */
public class ChildService {

  @Inject
  private ClientDao clientDao;
  private ChildMapper childMapper;

  @UnitOfWork(CMS)
  public ChildDto findByExternalId(String id) {
    Client client = clientDao.find(id);
    if (!client.getChildClientIndicator()) {
      throw new IllegalArgumentException("The client with ID:" + id + " is not a child");
    }
    return childMapper.toDto(client);
  }

}

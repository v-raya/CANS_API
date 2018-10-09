package gov.ca.cwds.cans.service;

import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.StaffClientDto;
import gov.ca.cwds.cans.domain.mapper.StaffClientsMapper;
import gov.ca.cwds.data.legacy.cms.dao.CaseDao;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

public class StaffService {

  @Inject private CaseDao caseDao;

  @Inject private StaffClientsMapper staffClientsMapper;

  @UnitOfWork(CMS)
  public Collection<StaffClientDto> getClientsByStaffId(final String staffId) {
    return caseDao
        .findClientsByStaffIdAndActiveDate(staffId, LocalDate.now())
        .stream()
        .map(staffClientsMapper::toDto)
        .collect(Collectors.toList());
  }
}

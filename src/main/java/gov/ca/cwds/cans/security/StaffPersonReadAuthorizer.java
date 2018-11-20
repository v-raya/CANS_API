package gov.ca.cwds.cans.security;

import gov.ca.cwds.data.legacy.cms.dao.StaffPersonDao;
import gov.ca.cwds.data.legacy.cms.entity.StaffPerson;
import gov.ca.cwds.data.legacy.cms.entity.facade.StaffBySupervisor;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import gov.ca.cwds.security.utils.PrincipalUtils;
import gov.ca.cwds.util.Require;
import java.util.Collection;
import javax.inject.Inject;

/** @author denys.davydov */
public class StaffPersonReadAuthorizer extends BaseAuthorizer<StaffPerson, String> {

  private final StaffPersonDao staffPersonDao;

  @Inject
  public StaffPersonReadAuthorizer(final StaffPersonDao staffPersonDao) {
    this.staffPersonDao = staffPersonDao;
  }

  @Override
  protected boolean checkId(final String staffId) {
    Require.requireNotNullAndNotEmpty(staffId);
    final String currentStaffId = PrincipalUtils.getPrincipal().getStaffId();
    if (staffId.equals(currentStaffId)) {
      return true;
    }
    final Collection<StaffBySupervisor> staffList =
        staffPersonDao.findStaffBySupervisorId(currentStaffId);
    return staffList.stream().anyMatch(staff -> staffId.equals(staff.getIdentifier()));
  }
}

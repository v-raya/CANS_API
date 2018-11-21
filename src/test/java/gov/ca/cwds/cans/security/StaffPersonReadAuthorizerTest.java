package gov.ca.cwds.cans.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import gov.ca.cwds.data.legacy.cms.dao.StaffPersonDao;
import gov.ca.cwds.data.legacy.cms.entity.facade.StaffBySupervisor;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import gov.ca.cwds.util.NullOrEmptyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/** @author denys.davydov */
@RunWith(PowerMockRunner.class)
@PrepareForTest(gov.ca.cwds.security.utils.PrincipalUtils.class)
public class StaffPersonReadAuthorizerTest {

  @Mock private StaffPersonDao staffPersonDao;
  private StaffPersonReadAuthorizer testSubject;

  @Before
  public void before() {
    testSubject = new StaffPersonReadAuthorizer(staffPersonDao);
  }

  @After
  public void after() {
    Mockito.reset(staffPersonDao);
  }

  @Test(expected = NullOrEmptyException.class)
  public void checkId_exception_whenStaffIdIsNull() {
    testSubject.checkId(null);
  }

  @Test(expected = NullOrEmptyException.class)
  public void checkId_exception_whenStaffIdIsEmptyString() {
    testSubject.checkId("");
  }

  @Test
  public void checkId_returnsTrue_whenStaffIdIsLoggedInUsersId() {
    mockPrincipalUtils("oki");
    final boolean actual = testSubject.checkId("oki");
    assertThat(actual, is(true));
  }

  @Test
  public void checkId_returnsTrue_whenStaffIdIsInSubordinateIdsList() {
    mockPrincipalUtils("supervisor");
    final List<StaffBySupervisor> staffList =
        Arrays.asList(
            newStaff("not_a_subordinate"),
            newStaff("subordinate"),
            newStaff("not_a_subordinate_two"));
    when(staffPersonDao.findStaffBySupervisorId(anyString())).thenReturn(staffList);
    final boolean actual = testSubject.checkId("subordinate");
    assertThat(actual, is(true));
  }

  @Test
  public void checkId_returnsFalse_whenSubordinateIdsListIsEmpty() {
    mockPrincipalUtils("supervisor");
    when(staffPersonDao.findStaffBySupervisorId(anyString())).thenReturn(Collections.emptyList());
    final boolean actual = testSubject.checkId("subordinate");
    assertThat(actual, is(false));
  }

  @Test
  public void checkId_returnsFalse_whenStaffIdIsNotInSubordinateIdsList() {
    mockPrincipalUtils("supervisor");
    final List<StaffBySupervisor> staffList =
        Arrays.asList(newStaff("not_a_subordinate_one"), newStaff("not_a_subordinate_two"));
    when(staffPersonDao.findStaffBySupervisorId(anyString())).thenReturn(staffList);
    final boolean actual = testSubject.checkId("subordinate");
    assertThat(actual, is(false));
  }

  private StaffBySupervisor newStaff(String subordinateId) {
    return new StaffBySupervisor(subordinateId, null, null, null, null, null);
  }

  private void mockPrincipalUtils(final String staffId) {
    final PerryAccount perryAccount = new PerryAccount();
    perryAccount.setStaffId(staffId);
    PowerMockito.mockStatic(PrincipalUtils.class);
    when(PrincipalUtils.getPrincipal()).thenReturn(perryAccount);
  }
}

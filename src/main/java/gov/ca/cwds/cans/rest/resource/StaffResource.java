package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.STAFF;
import static gov.ca.cwds.cans.Constants.API.SUBORDINATES;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.StaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.shiro.authz.annotation.RequiresPermissions;

@Api(value = STAFF, tags = STAFF)
@Path(value = STAFF)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StaffResource {

  private final StaffService staffService;

  @Inject
  public StaffResource(StaffService staffService) {
    this.staffService = staffService;
  }

  @GET
  @Path(SUBORDINATES)
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(
      value =
          "Get all subordinates with assessment statistics for a logged in user who has a supervisor priveleges",
      response = StaffStatisticsDto[].class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response getSubordinatesBySupervisor() {
    final Collection<StaffStatisticsDto> staffStatistics =
        staffService.getStaffStatisticsBySupervisor();
    return ResponseUtil.responseOk(staffStatistics);
  }
}

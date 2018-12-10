package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.STAFF;
import static gov.ca.cwds.cans.Constants.API.SUBORDINATES;
import static gov.ca.cwds.cans.Constants.CansPermissions.CANS_STAFF_PERSON_CLIENTS_READ;
import static gov.ca.cwds.cans.Constants.CansPermissions.CANS_STAFF_PERSON_READ;
import static gov.ca.cwds.cans.Constants.CansPermissions.CANS_STAFF_PERSON_SUBORDINATES_READ;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.StaffService;
import gov.ca.cwds.cans.validation.ValidStaffId;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collection;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

  @UnitOfWork(CANS)
  @GET
  @Path(SUBORDINATES)
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(
      value =
          "Get all subordinates with assessment statistics for a logged in user who has a supervisor privileges",
      response = StaffStatisticsDto[].class)
  @RequiresPermissions({CANS_ROLLOUT_PERMISSION, CANS_STAFF_PERSON_SUBORDINATES_READ})
  @Timed
  public Response getSubordinatesBySupervisor() {
    final Collection<StaffStatisticsDto> staffStatistics =
        staffService.getStaffStatisticsBySupervisor();
    return ResponseUtil.responseOk(staffStatistics);
  }

  @UnitOfWork(CANS)
  @GET
  @Path("{" + ID + "}")
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not found"),
        @ApiResponse(code = 422, message = "Unprocessable Entity")
      })
  @ApiOperation(
      value = "Get staff person with assessment statistics",
      response = StaffStatisticsDto.class)
  @RequiresPermissions({CANS_ROLLOUT_PERMISSION, CANS_STAFF_PERSON_READ})
  @Timed
  public Response getStaffPersonWithStatistics(
      @ApiParam(required = true, name = ID, value = "Staff id", example = "0X5")
          @PathParam(ID)
          @ValidStaffId
          @NotNull
          String staffId) {
    final StaffStatisticsDto result = staffService.getStaffPersonWithStatistics(staffId);
    return ResponseUtil.responseOrNotFound(result);
  }

  @UnitOfWork(CANS)
  @GET
  @Path("{" + ID + "}/" + PEOPLE)
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not found"),
        @ApiResponse(code = 422, message = "Unprocessable Entity")
      })
  @ApiOperation(
      value = "Get all clients by staff person identifier",
      response = StaffClientDto[].class)
  @RequiresPermissions({CANS_ROLLOUT_PERMISSION, CANS_STAFF_PERSON_CLIENTS_READ})
  @Timed
  public Collection<StaffClientDto> findPersonsByStaffId(
      @ApiParam(required = true, name = ID, value = "Staff id", example = "0X5")
          @PathParam(ID)
          @ValidStaffId
          @NotNull
          String staffId) {
    return staffService.findAssignedPersonsForStaffId(staffId);
  }

  @UnitOfWork(CANS)
  @GET
  @Path(ASSESSMENTS)
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(
      value = "Get assessment records created/updated by the logged in user ONLY",
      response = AssessmentDto[].class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response getStaffAssessments() {
    return ResponseUtil.responseOk(staffService.findAssessmentsByCurrentUser());
  }
}

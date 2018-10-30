package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.STAFF;
import static gov.ca.cwds.cans.Constants.API.SUBORDINATES;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.dto.assessment.SearchAssessmentRequest;
import gov.ca.cwds.cans.domain.dto.facade.StaffStatisticsDto;
import gov.ca.cwds.cans.domain.dto.person.StaffClientDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.mapper.AssessmentMapper;
import gov.ca.cwds.cans.domain.mapper.search.SearchAssessmentRequestMapper;
import gov.ca.cwds.cans.domain.search.SearchAssessmentParameters;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.AssessmentService;
import gov.ca.cwds.cans.service.StaffService;
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
import javax.ws.rs.POST;
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
  private final AssessmentService assessmentService;
  private final AssessmentMapper assessmentMapper;
  private final SearchAssessmentRequestMapper searchAssessmentMapper;

  @Inject
  public StaffResource(
      StaffService staffService,
      AssessmentService assessmentService,
      AssessmentMapper assessmentMapper,
      SearchAssessmentRequestMapper searchAssessmentMapper) {
    this.staffService = staffService;
    this.assessmentService = assessmentService;
    this.assessmentMapper = assessmentMapper;
    this.searchAssessmentMapper = searchAssessmentMapper;
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
          "Get all subordinates with assessment statistics for a logged in user who has a supervisor privileges",
      response = StaffStatisticsDto[].class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response getSubordinatesBySupervisor() {
    final Collection<StaffStatisticsDto> staffStatistics =
        staffService.getStaffStatisticsBySupervisor();
    return ResponseUtil.responseOk(staffStatistics);
  }

  @GET
  @Path("{" + ID + "}/" + PEOPLE)
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Get all clients from assigned cases", response = StaffClientDto[].class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Collection<StaffClientDto> findPersonsByStaffId(
      @ApiParam(required = true, name = ID, value = "Staff id", example = "0X5") @PathParam(ID)
          String staffId) {
    return staffService.findAssignedPersonsForStaffId(staffId);
  }

  @UnitOfWork(CANS)
  @POST
  @Path(ASSESSMENTS)
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(
      value = "Get all assessments, returns records created by the logged in user ONLY",
      response = AssessmentDto[].class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response search(
      @ApiParam(
              required = true,
              name = "Search Parameters",
              value = "Search assessments parameters")
          @NotNull
          final SearchAssessmentRequest searchRequest) {
    final SearchAssessmentParameters searchAssessmentParameters =
        searchAssessmentMapper.fromSearchRequest(searchRequest);
    final Collection<Assessment> entities =
        assessmentService.getAllAssessments(searchAssessmentParameters);
    final Collection<AssessmentMetaDto> dtos = assessmentMapper.toShortDtos(entities);
    return ResponseUtil.responseCreatedOrNot(dtos);
  }
}

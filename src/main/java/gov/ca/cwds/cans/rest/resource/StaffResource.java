package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.CLIENTS;
import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.STAFF;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CMS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.StaffClientDto;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.StaffService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

  @UnitOfWork(CMS)
  @GET
  @Path("/{" + ID + "}/" + CLIENTS)
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(
      value = "Get Clients assigned to the Staff Person",
      response = StaffClientDto[].class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response get(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Staff Person Id", example = "0Ki")
          final String staffId) {
    return ResponseUtil.responseOk(staffService.getClientsByStaffId(staffId));
  }
}

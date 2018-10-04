package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.CHECK_PERMISSION;
import static gov.ca.cwds.cans.Constants.API.SECURITY;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.service.SecurityService;
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

@Api(value = SECURITY, tags = SECURITY)
@Path(value = SECURITY)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SecurityResource {

  private final SecurityService securityService;

  @Inject
  public SecurityResource(SecurityService securityService) {
    this.securityService = securityService;
  }

  @UnitOfWork(CANS)
  @GET
  @Path("/" + CHECK_PERMISSION + "/{permission}")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Permission check status: true/false"),
      })
  @ApiOperation(value = "Check permission", response = Boolean.class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response checkPermission(
      @ApiParam(
              required = true,
              name = "permission",
              value = "The Assessment permission",
              example = "assessment:write:1")
          @PathParam("permission")
          String permission) {
    return Response.ok(securityService.checkPermission(permission)).build();
  }
}

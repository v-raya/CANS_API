package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.CHILDREN;
import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.person.ChildDto;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.ChildrenService;
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

/** @author TPT-2 Team */
@Api(value = CHILDREN, tags = PEOPLE)
@Path(value = CHILDREN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChildrenResource {

  private final ChildrenService childrenService;

  @Inject
  public ChildrenResource(ChildrenService childrenService) {
    this.childrenService = childrenService;
  }

  @GET
  @Path("/{" + ID + "}")
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Get Child by cms id", response = ChildDto.class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response get(
      @PathParam(ID)
          @ApiParam(
              required = true,
              name = ID,
              value = "The CMS Client identifier",
              example = "AasRx3r0Ha")
          final String id) {
    return ResponseUtil.responseOrNotFound(childrenService.findByExternalId(id));
  }
}

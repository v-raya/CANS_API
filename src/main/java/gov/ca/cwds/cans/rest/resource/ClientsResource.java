package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.CLIENTS;
import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.CansPermissions.CANS_CLIENT_READ;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.ClientsService;
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

/** @author TPT-2 Team */
@Api(value = CLIENTS, tags = PEOPLE)
@Path(value = CLIENTS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientsResource {

  private final ClientsService clientsService;

  @Inject
  public ClientsResource(ClientsService clientsService) {
    this.clientsService = clientsService;
  }

  @UnitOfWork(CANS)
  @GET
  @Path("/{" + ID + "}")
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Get Child by cms id", response = ClientDto.class)
  @RequiresPermissions({CANS_ROLLOUT_PERMISSION, CANS_CLIENT_READ})
  @Timed
  public Response get(
      @PathParam(ID)
          @ApiParam(
              required = true,
              name = ID,
              value = "The CMS Client identifier",
              example = "AasRx3r0Ha")
          final String id) {
    return ResponseUtil.responseOrNotFound(clientsService.findByExternalId(id));
  }
}

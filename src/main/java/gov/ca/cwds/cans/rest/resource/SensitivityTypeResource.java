package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.SENSITIVITY_TYPES;
import static gov.ca.cwds.cans.Constants.Privileges.SEALED;
import static gov.ca.cwds.cans.Constants.Privileges.SENSITIVE_PERSONS;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;

import com.codahale.metrics.annotation.Timed;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.security.realm.PerryAccount;
import gov.ca.cwds.security.utils.PrincipalUtils;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/** @author volodymyr.petrusha */
@Api(value = SENSITIVITY_TYPES, tags = SENSITIVITY_TYPES)
@Path(value = SENSITIVITY_TYPES)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensitivityTypeResource {

  @UnitOfWork(CANS)
  @GET
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Get Sensitivity Type", response = SensitivityType[].class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response get(
      @ApiParam(name = "county", value = "The county external id related to the Client")
          @QueryParam("county")
          String clientCountyExternalId) {
    return ResponseUtil.responseOk(getAllowedSensitivityTypes(clientCountyExternalId));
  }

  private List<SensitivityType> getAllowedSensitivityTypes(String clientCountyExternalId) {
    PerryAccount principal = PrincipalUtils.getPrincipal();
    Set<String> privileges = principal.getPrivileges();
    return principal.getCountyCode().equals(clientCountyExternalId)
        ? Arrays.stream(SensitivityType.values())
            .filter(
                value -> {
                  boolean result =
                      value == SensitivityType.SENSITIVE && privileges.contains(SENSITIVE_PERSONS);
                  result |= value == SensitivityType.SEALED && privileges.contains(SEALED);
                  return result;
                })
            .collect(Collectors.toList())
        : Collections.emptyList();
  }
}

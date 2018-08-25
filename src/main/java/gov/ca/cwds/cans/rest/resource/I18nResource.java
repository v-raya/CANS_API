package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.I18N;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_WORKER_ROLE;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.entity.I18n;
import gov.ca.cwds.cans.domain.mapper.I18nMapper;
import gov.ca.cwds.cans.service.I18nService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collection;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

/** @author denys.davydov */
@Api(value = I18N, tags = I18N)
@Path(value = I18N)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class I18nResource {

  private final I18nService i18nService;
  private final I18nMapper i18nMapper;

  @Inject
  public I18nResource(I18nService i18nService, I18nMapper i18nMapper) {
    this.i18nService = i18nService;
    this.i18nMapper = i18nMapper;
  }

  @UnitOfWork(CANS)
  @GET
  @Path("/{keyPrefix}/{" + API.I18N_LANG_PARAM + "}")
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Get i18n map by key prefix and language", response = Map.class)
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response findByKeyPrefixAndLanguage(
      @PathParam(value = "keyPrefix")
          @ApiParam(
            required = true,
            name = "keyPrefix",
            value = "The prefix of the key to search i18n records by",
            example = "instrument.1."
          )
          final String keyPrefix,
      @PathParam(value = API.I18N_LANG_PARAM)
          @ApiParam(
            required = true,
            name = API.I18N_LANG_PARAM,
            value = "The language of i18n",
            example = "en"
          )
          final String lang) {
    final Collection<I18n> records = i18nService.findByKeyPrefixAndLanguage(keyPrefix, lang);
    final Map<String, String> resultMap = i18nMapper.toMap(records);
    return Response.ok(resultMap).build();
  }

  @UnitOfWork(CANS)
  @GET
  @Path("/{keyPrefix}")
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Get i18n map by key prefix and default language", response = Map.class)
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response findByKeyPrefixAndDefaultLanguage(
      @PathParam(value = "keyPrefix")
          @ApiParam(
            required = true,
            name = "keyPrefix",
            value = "The prefix of the key to search i18n records by",
            example = "instrument.1."
          )
          final String keyPrefix) {
    final Collection<I18n> records = i18nService.findByKeyPrefixAndLanguage(keyPrefix, "en");
    final Map<String, String> resultMap = i18nMapper.toMap(records);
    return Response.ok(resultMap).build();
  }
}

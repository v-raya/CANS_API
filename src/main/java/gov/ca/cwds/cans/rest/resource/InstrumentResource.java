package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.INSTRUMENTS;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_WORKER_ROLE;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.Constants;
import gov.ca.cwds.cans.Constants.API;
import gov.ca.cwds.cans.domain.dto.InstrumentDto;
import gov.ca.cwds.cans.domain.entity.I18n;
import gov.ca.cwds.cans.domain.entity.Instrument;
import gov.ca.cwds.cans.domain.mapper.I18nMapper;
import gov.ca.cwds.cans.domain.mapper.InstrumentMapper;
import gov.ca.cwds.cans.service.I18nService;
import gov.ca.cwds.cans.service.InstrumentService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collection;
import java.util.Map;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

/** @author denys.davydov */
@Api(value = INSTRUMENTS, tags = INSTRUMENTS)
@Path(value = INSTRUMENTS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InstrumentResource {

  private final I18nService i18nService;
  private final I18nMapper i18nMapper;
  private final ACrudResource<Instrument, InstrumentDto> crudResource;

  @Inject
  public InstrumentResource(
      InstrumentService instrumentService,
      InstrumentMapper instrumentMapper,
      I18nService i18nService,
      I18nMapper i18nMapper) {
    this.i18nService = i18nService;
    this.i18nMapper = i18nMapper;
    crudResource = new ACrudResource<>(instrumentService, instrumentMapper);
  }

  @UnitOfWork(CANS)
  @POST
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Post new Instrument", response = InstrumentDto.class)
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response post(
      @ApiParam(name = "Instrument", value = "The Instrument object") @Valid
          final InstrumentDto dto) {
    return crudResource.post(dto);
  }

  @UnitOfWork(CANS)
  @PUT
  @Path("/{" + ID + "}")
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Update existent Instrument", response = InstrumentDto.class)
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response put(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Instrument id", example = "50000")
          final Long id,
      @ApiParam(name = "Instrument", value = "The Instrument object") @Valid
          final InstrumentDto dto) {
    return crudResource.put(id, dto);
  }

  @UnitOfWork(CANS)
  @GET
  @Path("/{" + ID + "}")
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Get Instrument by id", response = InstrumentDto.class)
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response get(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Instrument id", example = "50000")
          final Long id) {
    return crudResource.get(id);
  }

  @UnitOfWork(CANS)
  @DELETE
  @Path("/{" + ID + "}")
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Delete Instrument by id", response = InstrumentDto.class)
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response delete(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Instrument id", example = "50000")
          final Long id) {
    return crudResource.delete(id);
  }

  @UnitOfWork(CANS)
  @GET
  @Path("/{" + ID + "}/i18n/{" + API.I18N_LANG_PARAM + "}")
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Get i18n map for Instrument by language", response = Map.class)
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response findByKeyPrefixAndLanguage(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Instrument id", example = "1")
          final Long id,
      @PathParam(value = API.I18N_LANG_PARAM)
          @ApiParam(
            required = true,
            name = API.I18N_LANG_PARAM,
            value = "The language of i18n",
            example = "en"
          )
          final String lang) {
    final String keyPrefix = Constants.INSTRUMENT_KEY_PREFIX + id + ".";
    final Collection<I18n> records = i18nService.findByKeyPrefixAndLanguage(keyPrefix, lang);
    final Map<String, String> resultMap = i18nMapper.toMapWithKeyPrefixCut(records, keyPrefix);
    return Response.ok(resultMap).build();
  }
}

package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.CONSTRUCTS;
import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.InstrumentDto;
import gov.ca.cwds.cans.domain.entity.Instrument;
import gov.ca.cwds.cans.domain.mapper.InstrumentMapper;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.InstrumentService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

/** @author denys.davydov */
@Api(value = CONSTRUCTS, tags = CONSTRUCTS)
@Path(value = CONSTRUCTS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InstrumentResource {
  private final InstrumentService instrumentService;
  private final InstrumentMapper instrumentMapper;

  @Inject
  public InstrumentResource(InstrumentService instrumentService, InstrumentMapper instrumentMapper) {
    this.instrumentService = instrumentService;
    this.instrumentMapper = instrumentMapper;
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
  @Timed
  public Response post(
      @ApiParam(name = "Instrument", value = "The Instrument object")
      @Valid
      final InstrumentDto inputDto
  ) {
    final Instrument inputEntity = instrumentMapper.fromDto(inputDto);
    final Instrument resultEntity = instrumentService.create(inputEntity);
    final InstrumentDto resultDto = instrumentMapper.toDto(resultEntity);
    return Response.ok().entity(resultDto).build();
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
  @Timed
  public Response put(
      @PathParam("id")
      @ApiParam(required = true, name = "id", value = "The Instrument id", example = "50000")
      final Long id,
      @ApiParam(name = "Instrument", value = "The Instrument object")
      @Valid
      final InstrumentDto inputDto
  ) {
    final Instrument inputEntity = instrumentMapper.fromDto(inputDto);
    inputEntity.setId(id);
    final Instrument resultEntity = instrumentService.update(inputEntity);
    final InstrumentDto resultDto = instrumentMapper.toDto(resultEntity);
    return Response.ok().entity(resultDto).build();
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
  @Timed
  public Response get(
      @PathParam("id")
      @ApiParam(required = true, name = "id", value = "The Instrument id", example = "50000")
      final Long id
  ) {
    final Instrument entity = instrumentService.read(id);
    final InstrumentDto dto = instrumentMapper.toDto(entity);
    return ResponseUtil.responseOrNotFound(dto);
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
  @Timed
  public Response delete(
      @PathParam("id")
      @ApiParam(required = true, name = "id", value = "The Instrument id", example = "50000")
      final Long id
  ) {
    instrumentService.delete(id);
    return Response.noContent().build();
  }
}

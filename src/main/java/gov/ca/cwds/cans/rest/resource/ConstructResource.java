package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.CONSTRUCTS;
import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.ConstructDto;
import gov.ca.cwds.cans.domain.entity.Construct;
import gov.ca.cwds.cans.domain.mapper.ConstructMapper;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.ConstructService;
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
public class ConstructResource {
  private final ConstructService constructService;
  private final ConstructMapper constructMapper;

  @Inject
  public ConstructResource(ConstructService constructService, ConstructMapper constructMapper) {
    this.constructService = constructService;
    this.constructMapper = constructMapper;
  }

  @UnitOfWork(CANS)
  @POST
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Post new Construct", response = ConstructDto.class)
  @Timed
  public Response post(
      @ApiParam(name = "Construct", value = "The Construct object")
      @Valid
      final ConstructDto inputDto
  ) {
    final Construct inputEntity = constructMapper.fromDto(inputDto);
    final Construct resultEntity = constructService.create(inputEntity);
    final ConstructDto resultDto = constructMapper.toDto(resultEntity);
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
  @ApiOperation(value = "Get Construct by id", response = ConstructDto.class)
  @Timed
  public Response get(
      @PathParam("id")
      @ApiParam(required = true, name = "id", value = "The Construct id", example = "50000")
      final Long id
  ) {
    final Construct entity = constructService.read(id);
    final ConstructDto dto = constructMapper.toDto(entity);
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
  @ApiOperation(value = "Delete Construct by id", response = ConstructDto.class)
  @Timed
  public Response delete(
      @PathParam("id")
      @ApiParam(required = true, name = "id", value = "The Construct id", example = "50000")
      final Long id
  ) {
    constructService.delete(id);
    return Response.noContent().build();
  }
}

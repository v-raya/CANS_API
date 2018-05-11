package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.PERSONS;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.mapper.PersonMapper;
import gov.ca.cwds.cans.service.PersonService;
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
@Api(value = PERSONS, tags = PERSONS)
@Path(value = PERSONS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {

  private final ACrudResource<Person, PersonDto> crudResource;

  @Inject
  public PersonResource(PersonService personService, PersonMapper personMapper) {
    crudResource = new ACrudResource<>(personService, personMapper);
  }

  @UnitOfWork(CANS)
  @POST
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Post new Person", response = PersonDto.class)
  @Timed
  public Response post(
      @ApiParam(name = "Person", value = "The Person object") @Valid final PersonDto dto) {
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
  @ApiOperation(value = "Update existent Person", response = PersonDto.class)
  @Timed
  public Response put(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Person id", example = "50000")
          final Long id,
      @ApiParam(name = "Person", value = "The Person object") @Valid final PersonDto dto) {
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
  @ApiOperation(value = "Get Person by id", response = PersonDto.class)
  @Timed
  public Response get(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Person id", example = "50000")
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
  @ApiOperation(value = "Delete Person by id", response = PersonDto.class)
  @Timed
  public Response delete(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Person id", example = "50000")
          final Long id) {
    return crudResource.delete(id);
  }
}

package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_WORKER_ROLE;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.domain.dto.person.SearchPersonRequest;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.mapper.PersonMapper;
import gov.ca.cwds.cans.domain.mapper.SearchPersonMapper;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.PersonService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
@Api(value = PEOPLE, tags = PEOPLE)
@Path(value = PEOPLE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {

  private final ACrudResource<Person, PersonDto> crudResource;
  private final PersonService personService;
  private final PersonMapper personMapper;
  private final SearchPersonMapper searchPersonMapper;

  @Inject
  public PersonResource(PersonService personService, PersonMapper personMapper, SearchPersonMapper searchPersonMapper) {
    crudResource = new ACrudResource<>(personService, personMapper);
    this.personService = personService;
    this.personMapper = personMapper;
    this.searchPersonMapper = searchPersonMapper;
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
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
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
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
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
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response get(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Person id", example = "50000")
          final Long id) {
    return crudResource.get(id);
  }

  @UnitOfWork(CANS)
  @GET
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
    }
  )
  @ApiOperation(value = "Get all people", response = PersonDto[].class)
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response getAll() {
    final Collection<Person> entities = personService.findAll();
    final Collection<PersonDto> dtos = personMapper.toDtos(entities);
    return ResponseUtil.responseOk(dtos);
  }

  @UnitOfWork(CANS)
  @POST
  @Path(SEARCH)
  @ApiResponses(
      value = {
          @ApiResponse(code = 401, message = "Not Authorized"),
      }
  )
  @ApiOperation(value = "Search people by parameters", response = PersonDto[].class)
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response search(
      @ApiParam(required = true, name = "Search Parameters", value = "Search People parameters")
      @NotNull
      final SearchPersonRequest searchRequest) {
    final SearchPersonPo searchPo = searchPersonMapper.fromSearchRequest(searchRequest);
    final Collection<Person> entities = personService.search(searchPo);
    final Collection<PersonDto> dtos = personMapper.toDtos(entities);
    return ResponseUtil.responseOk(dtos);
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
  @RequiresRoles(CANS_WORKER_ROLE)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response delete(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Person id", example = "50000")
          final Long id) {
    return crudResource.delete(id);
  }

}

package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static gov.ca.cwds.cans.Constants.API.SEARCH;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;
import static gov.ca.cwds.cans.rest.auth.CansStaticAuthorizer.CANS_ROLLOUT_PERMISSION;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.domain.dto.person.SearchPersonRequest;
import gov.ca.cwds.cans.domain.dto.person.SearchPersonResponse;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.mapper.PersonMapper;
import gov.ca.cwds.cans.domain.mapper.search.SearchPersonRequestMapper;
import gov.ca.cwds.cans.domain.mapper.search.SearchPersonResponseMapper;
import gov.ca.cwds.cans.domain.search.SearchPersonParameters;
import gov.ca.cwds.cans.domain.search.SearchPersonResult;
import gov.ca.cwds.cans.exception.DuplicationException;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.PersonService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

/** @author denys.davydov */
@Api(value = PEOPLE, tags = PEOPLE)
@Path(value = PEOPLE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {

  private final ACrudResource<Person, PersonDto> crudResource;
  private final PersonService personService;
  private final SearchPersonRequestMapper searchPersonParametersMapper;
  private final SearchPersonResponseMapper searchPersonMapper;

  @Inject
  public PersonResource(
      PersonService personService,
      PersonMapper personMapper,
      SearchPersonResponseMapper searchPersonMapper,
      SearchPersonRequestMapper searchPersonParametersMapper) {
    crudResource = new ACrudResource<>(personService, personMapper);
    this.personService = personService;
    this.searchPersonMapper = searchPersonMapper;
    this.searchPersonParametersMapper = searchPersonParametersMapper;
  }

  @UnitOfWork(CANS)
  @POST
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Post new Person", response = PersonDto.class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response post(
      @ApiParam(name = "Person", value = "The Person object") @Valid final PersonDto dto) {
    Person duplicatePerson = personService.findByExternalId(dto.getExternalId());
    if (duplicatePerson != null) {
      throwDuplicationException(duplicatePerson);
    }
    return crudResource.post(dto);
  }

  @UnitOfWork(CANS)
  @PUT
  @Path("/{" + ID + "}")
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Update existent Person", response = PersonDto.class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response put(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Person id", example = "50000")
          final Long id,
      @ApiParam(name = "Person", value = "The Person object") @Valid final PersonDto dto) {
    Person duplicatePerson = personService.findByExternalId(dto.getExternalId());
    if (duplicatePerson != null && !duplicatePerson.getId().equals(dto.getId())) {
      throwDuplicationException(duplicatePerson);
    }
    return crudResource.put(id, dto);
  }

  @UnitOfWork(CANS)
  @GET
  @Path("/{" + ID + "}")
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Get Person by id", response = PersonDto.class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response get(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Person id", example = "50000")
          final Long id) {
    return crudResource.get(id);
  }

  @UnitOfWork(CANS)
  @POST
  @Path(SEARCH)
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
      })
  @ApiOperation(value = "Search people by parameters", response = PersonDto[].class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response search(
      @ApiParam(required = true, name = "Search Parameters", value = "Search People parameters")
          @NotNull
          @Valid
          final SearchPersonRequest searchRequest) {
    final SearchPersonParameters searchParameters =
        searchPersonParametersMapper.fromSearchRequest(searchRequest);
    final SearchPersonResult searchPersonResult = personService.search(searchParameters);
    final SearchPersonResponse searchPersonResponse = searchPersonMapper.toDto(searchPersonResult);
    return ResponseUtil.responseOk(searchPersonResponse);
  }

  @UnitOfWork(CANS)
  @DELETE
  @Path("/{" + ID + "}")
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found")
      })
  @ApiOperation(value = "Delete Person by id", response = PersonDto.class)
  @RequiresPermissions(CANS_ROLLOUT_PERMISSION)
  @Timed
  public Response delete(
      @PathParam(ID)
          @ApiParam(required = true, name = ID, value = "The Person id", example = "50000")
          final Long id) {
    return crudResource.delete(id);
  }

  private void throwDuplicationException(Person person) {
    throw new DuplicationException(
        "This Client ID #"
            + person.getExternalId()
            + " already exists in "
            + person.getCounty().getName()
            + " County");
  }
}

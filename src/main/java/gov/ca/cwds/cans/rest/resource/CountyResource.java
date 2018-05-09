package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.COUNTIES;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.domain.mapper.CountyMapper;
import gov.ca.cwds.cans.service.CountyService;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** @author denys.davydov */
@Api(value = COUNTIES, tags = COUNTIES)
@Path(value = COUNTIES)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CountyResource {

  private final CountyService countyService;
  private final CountyMapper countyMapper;

  @Inject
  public CountyResource(CountyService countyService, CountyMapper countyMapper) {
    this.countyService = countyService;
    this.countyMapper = countyMapper;
  }

  @UnitOfWork(CANS)
  @GET
  @ApiResponses(
    value = {
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found")
    }
  )
  @ApiOperation(value = "Get all counties", response = CountyDto[].class)
  @Timed
  public Response getAll() {
    final Collection<County> counties = countyService.findAll();
    final Collection<CountyDto> dtos = countyMapper.toDtos(counties);
    return ResponseUtil.responseOk(dtos);
  }
}

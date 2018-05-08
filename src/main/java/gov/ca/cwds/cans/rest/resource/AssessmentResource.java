package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;
import static gov.ca.cwds.cans.Constants.API.ID;
import static gov.ca.cwds.cans.Constants.API.START;
import static gov.ca.cwds.cans.Constants.UnitOfWork.CANS;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cans.domain.dto.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.StartAssessmentRequest;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.mapper.AssessmentMapper;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.AssessmentService;
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

/**
 * @author denys.davydov
 */
@Api(value = ASSESSMENTS, tags = ASSESSMENTS)
@Path(value = ASSESSMENTS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AssessmentResource {
  private final AssessmentService assessmentService;
  private final AssessmentMapper assessmentMapper;

  @Inject
  public AssessmentResource(AssessmentService assessmentService, AssessmentMapper assessmentMapper) {
    this.assessmentService = assessmentService;
    this.assessmentMapper = assessmentMapper;
  }

  @UnitOfWork(CANS)
  @POST
  @ApiResponses(
      value = {
          @ApiResponse(code = 401, message = "Not Authorized"),
          @ApiResponse(code = 404, message = "Not found")
      }
  )
  @ApiOperation(value = "Post new Assessment", response = AssessmentDto.class)
  @Timed
  public Response post(
      @ApiParam(name = "Assessment", value = "The Assessment object")
      @Valid
      final AssessmentDto inputDto
  ) {
    final Assessment inputEntity = assessmentMapper.fromDto(inputDto);
    final Assessment resultEntity = assessmentService.create(inputEntity);
    final AssessmentDto resultDto = assessmentMapper.toDto(resultEntity);
    return Response.ok().entity(resultDto).build();
  }

  @UnitOfWork(CANS)
  @POST
  @Path(START)
  @ApiResponses(
      value = {
          @ApiResponse(code = 401, message = "Not Authorized"),
          @ApiResponse(code = 404, message = "Not found")
      }
  )
  @ApiOperation(value = "Start new Assessment", response = AssessmentDto.class)
  @Timed
  public Response start(
      @ApiParam(name = "Assessment", value = "The Assessment object")
      @Valid
      final StartAssessmentRequest request
  ) {
    final Assessment assessment = assessmentService.start(request);
    final AssessmentDto resultDto = assessmentMapper.toDto(assessment);
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
  @ApiOperation(value = "Update existent Assessment", response = AssessmentDto.class)
  @Timed
  public Response put(
      @PathParam("id")
      @ApiParam(required = true, name = "id", value = "The Assessment id", example = "50000")
      final Long id,
      @ApiParam(name = "Assessment", value = "The Assessment object")
      @Valid
      final AssessmentDto inputDto
  ) {
    final Assessment inputEntity = assessmentMapper.fromDto(inputDto);
    inputEntity.setId(id);
    final Assessment resultEntity = assessmentService.update(inputEntity);
    final AssessmentDto resultDto = assessmentMapper.toDto(resultEntity);
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
  @ApiOperation(value = "Get Assessment by id", response = AssessmentDto.class)
  @Timed
  public Response get(
      @PathParam("id")
      @ApiParam(required = true, name = "id", value = "The Assessment id", example = "50000")
      final Long id
  ) {
    final Assessment entity = assessmentService.read(id);
    final AssessmentDto dto = assessmentMapper.toDto(entity);
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
  @ApiOperation(value = "Delete Assessment by id", response = AssessmentDto.class)
  @Timed
  public Response delete(
      @PathParam("id")
      @ApiParam(required = true, name = "id", value = "The Assessment id", example = "50000")
      final Long id
  ) {
    assessmentService.delete(id);
    return Response.noContent().build();
  }

}

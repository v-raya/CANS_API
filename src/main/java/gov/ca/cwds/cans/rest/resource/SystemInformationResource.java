package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.SYSTEM_INFORMATION;

import com.google.inject.Inject;
import gov.ca.cwds.cans.service.dto.system.SystemInformationDto;
import gov.ca.cwds.cans.service.SystemInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author denys.davydov
 */
@Api(value = SYSTEM_INFORMATION)
@Path(SYSTEM_INFORMATION)
@Produces(MediaType.APPLICATION_JSON)
public class SystemInformationResource {

  private final SystemInformationService systemInformationService;

  @Inject
  public SystemInformationResource(final SystemInformationService systemInformationService) {
    this.systemInformationService = systemInformationService;
  }

  /**
   * Get application state information
   *
   * @return the application data
   */
  @GET
  @ApiOperation(value = "Returns System Information", response = SystemInformationDto.class)
  public SystemInformationDto get() {
    return systemInformationService.getSystemInformation();
  }
}

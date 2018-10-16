package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.SYSTEM_INFORMATION;

import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.cans.CansConfiguration;
import gov.ca.cwds.dto.app.SystemInformationDto;
import gov.ca.cwds.rest.api.ApiException;
import gov.ca.cwds.rest.resources.system.AbstractSystemInformationResource;
import io.dropwizard.setup.Environment;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** @author denys.davydov */
@Api(value = SYSTEM_INFORMATION)
@Path(SYSTEM_INFORMATION)
@Produces(MediaType.APPLICATION_JSON)
public class SystemInformationResource extends AbstractSystemInformationResource {

  private static final String VERSION_PROPERTIES_FILE = "version.properties";
  private static final String BUILD_NUMBER = "build.number";
  private static final String BUILD_VERSION = "build.version";

  @Inject
  public SystemInformationResource(
      final CansConfiguration configuration, final Environment environment) {
    super(environment.healthChecks());
    super.applicationName = configuration.getApplicationName();
    final Properties versionProperties = getVersionProperties();
    super.version = versionProperties.getProperty(BUILD_VERSION);
    super.buildNumber = versionProperties.getProperty(BUILD_NUMBER);
  }

  @SuppressFBWarnings({"EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS"})
  private Properties getVersionProperties() {
    final Properties versionProperties = new Properties();
    try (InputStream is = ClassLoader.getSystemResourceAsStream(VERSION_PROPERTIES_FILE)) {
      versionProperties.load(is);
    } catch (IOException e) {
      throw new ApiException("Can't read version.properties", e);
    }
    return versionProperties;
  }

  /**
   * Get application state information
   *
   * @return the application data
   */
  @GET
  @ApiResponses(
      value = {
        @ApiResponse(code = 401, message = "Not Authorized"),
        @ApiResponse(code = 404, message = "Not found"),
        @ApiResponse(code = 465, message = "CARES Service is not healthy")
      })
  @ApiOperation(value = "Returns System Information", response = SystemInformationDto.class)
  public Response get() {
    return super.buildResponse();
  }
}

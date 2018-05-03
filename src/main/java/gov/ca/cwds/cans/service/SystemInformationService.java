package gov.ca.cwds.cans.service;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;
import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.cans.CansConfiguration;
import gov.ca.cwds.cans.Constants;
import gov.ca.cwds.cans.rest.dto.system.HealthCheckResultDto;
import gov.ca.cwds.cans.rest.dto.system.SystemInformationDto;
import gov.ca.cwds.rest.api.ApiException;
import io.dropwizard.setup.Environment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author denys.davydov
 */
public class SystemInformationService {

  private static final String VERSION_PROPERTIES_FILE = "version.properties";
  private static final String BUILD_NUMBER = "build.number";
  private static final String BUILD_VERSION = "build.version";

  private final String applicationName;
  private final String applicationVersion;
  private final Environment environment;
  private final String buildNumber;

  @Inject
  public SystemInformationService(
      final CansConfiguration configuration,
      final Environment environment) {
    this.applicationName = configuration.getApplicationName();
    this.environment = environment;
    final Properties versionProperties = getVersionProperties();
    this.applicationVersion = versionProperties.getProperty(BUILD_VERSION);
    this.buildNumber = versionProperties.getProperty(BUILD_NUMBER);
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

  public SystemInformationDto getSystemInformation() {
    final SystemInformationDto systemInformation = new SystemInformationDto();
    systemInformation.setApplication(applicationName);
    systemInformation.setVersion(applicationVersion);
    systemInformation.setBuildNumber(buildNumber);

    final Map<String, Result> healthChecks = environment.healthChecks().runHealthChecks();
//    systemInformation.setCans(getHealthCheckResultDto(healthChecks.get(Constants.UnitOfWork.CANS)));
    systemInformation.setDeadlocks(getHealthCheckResultDto(healthChecks.get("deadlocks")));

    return systemInformation;
  }

  private HealthCheckResultDto getHealthCheckResultDto(HealthCheck.Result result) {
    HealthCheckResultDto healthCheckResultDto = new HealthCheckResultDto();
    healthCheckResultDto.setResult(result);
    return healthCheckResultDto;
  }

}

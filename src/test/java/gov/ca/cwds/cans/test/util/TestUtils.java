package gov.ca.cwds.cans.test.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author denys.davydov
 */
public class TestUtils {

  public static final String API_URL = "api.url";
  public static final String PERRY_URL = "perry.url";
  public static final String PERRY_LOGIN_FORM_URL = "login.form.target.url";
  public static final String SLASH = "/";
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private TestUtils() {
  }

  public static String getApiUrl() {
    return getUrlFromSystemProperties(API_URL);
  }

  public static String getPerryUrl() {
    return getUrlFromSystemProperties(PERRY_URL);
  }

  public static String getPerryLoginFormUrl() {
    return System.getProperty(PERRY_LOGIN_FORM_URL);
  }

  public static String getUrlFromSystemProperties(String systemProperty) {
    final String urlRaw = System.getProperty(systemProperty);
    return addTrailingSlashIfNeeded(urlRaw);
  }

  private static String addTrailingSlashIfNeeded(String url) {
    return url == null || url.endsWith(SLASH) ? url : url + SLASH;
  }

  public static LocalDate localDate(String dateStr) {
    return LocalDate.parse(dateStr, DATE_FORMATTER);
  }
}

package gov.ca.cwds.cans;

/** @author denys.davydov */
public final class Constants {

  public static final String INSTRUMENT_KEY_PREFIX = "instrument.";
  public static final String SQL_TYPE = "sqlType";
  public static final String RETURNED_CLASS_NAME_PARAM = "returnedClassName";
  public static final String EDITABLE = "editable";

  private Constants() {}

  public static class API {

    public static final String ID = "id";

    public static final String SYSTEM_INFORMATION = "system-information";

    public static final String SEARCH = "_search";
    public static final String COUNTIES = "counties";
    public static final String PEOPLE = "people";
    public static final String STAFF = "staff";
    public static final String SUBORDINATES = "subordinates";
    public static final String INSTRUMENTS = "instruments";
    public static final String ASSESSMENTS = "assessments";
    public static final String SECURITY = "security";
    public static final String CHECK_PERMISSION = "check_permission";
    public static final String I18N = "i18n";
    public static final String I18N_LANG_PARAM = "lang";
    public static final String SENSITIVITY_TYPES = "sensitivity-types";

    private API() {}
  }

  public static class UnitOfWork {
    public static final String CANS = "cans";
    public static final String CMS = "cwscms";
    public static final String CMS_RS = "cwscmsrs";

    private UnitOfWork() {}
  }

  public static class Privileges {
    public static final String SENSITIVE_PERSONS = "Sensitive Persons";
    public static final String SEALED = "Sealed";

    private Privileges() {}
  }

  public static class MagicNumbers {
    public static final String STATE_OF_CALIFORNIA_CODE = "1126";

    private MagicNumbers() {}
  }
}

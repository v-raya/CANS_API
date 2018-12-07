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
    public static final String CLIENTS = "clients";
    public static final String STAFF = "staff";
    public static final String SUBORDINATES = "subordinates";
    public static final String INSTRUMENTS = "instruments";
    public static final String ASSESSMENTS = "assessments";
    public static final String SECURITY = "security";
    public static final String CHECK_PERMISSION = "check_permission";
    public static final String I18N = "i18n";
    public static final String I18N_LANG_PARAM = "lang";
    public static final String SENSITIVITY_TYPES = "sensitivity-types";
    public static final String CHANGELOG = "changelog";

    private API() {}
  }

  public static class UnitOfWork {
    public static final String CANS = "cans";
    public static final String CMS = "cwscms";
    public static final String CMS_RS = "cwscmsrs";

    private UnitOfWork() {}
  }

  public static class Roles {
    public static final String SUPERVISOR = "Supervisor";
    public static final String SOCIAL_WORKER = "SocialWorker";

    private Roles() {}
  }

  public static class CansPermissions {
    public static final String CANS_STAFF_PERSON_SUBORDINATES_READ =
        "CANS-staff-person-subordinates-read";
    public static final String CANS_STAFF_PERSON_READ = "CANS-staff-person-read";
    public static final String CANS_STAFF_PERSON_CLIENTS_READ = "CANS-staff-person-clients-read";
    public static final String CANS_CLIENT_READ = "CANS-client-read";
    public static final String CANS_CLIENT_SEARCH = "CANS-client-search";
    public static final String CANS_ASSESSMENT_READ = "CANS-assessment-read";
    public static final String CANS_ASSESSMENT_CREATE = "CANS-assessment-create";
    public static final String CANS_ASSESSMENT_IN_PROGRESS_UPDATE =
        "CANS-assessment-in-progress-update";
    public static final String CANS_ASSESSMENT_COMPLETED_UPDATE =
        "CANS-assessment-completed-update";
    public static final String CANS_ASSESSMENT_COMPLETED_DELETE =
        "CANS-assessment-completed-delete";
    public static final String CANS_ASSESSMENT_IN_PROGRESS_DELETE =
        "CANS-assessment-in-progress-delete";
    public static final String CANS_ASSESSMENT_COMPLETE = "CANS-assessment-complete";

    private CansPermissions() {}
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

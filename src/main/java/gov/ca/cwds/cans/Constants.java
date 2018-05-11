package gov.ca.cwds.cans;

/** @author denys.davydov */
public final class Constants {

  private Constants() {}

  public static final String INSTRUMENT_KEY_PREFIX = "instrument.";

  public static final String SQL_TYPE = "sqlType";

  public static final String RETURNED_CLASS_NAME_PARAM = "returnedClassName";

  public static class API {

    public static final String ID = "id";

    public static final String SYSTEM_INFORMATION = "system-information";

    public static final String COUNTIES = "counties";
    public static final String PERSONS = "persons";
    public static final String INSTRUMENTS = "instruments";
    public static final String ASSESSMENTS = "assessments";
    public static final String START = "_start";
    public static final String I18N = "i18n";
    public static final String I18N_LANG_PARAM = "lang";

    private API() {}
  }

  public static class ExpectedExceptionMessages {

    private ExpectedExceptionMessages() {}
  }

  public static class Validation {

    private Validation() {}
  }

  public static class UnitOfWork {

    public static final String CANS = "cans";

    private UnitOfWork() {}
  }
}

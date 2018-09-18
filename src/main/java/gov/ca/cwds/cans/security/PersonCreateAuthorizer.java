package gov.ca.cwds.cans.security;

import gov.ca.cwds.drools.DroolsConfiguration;

public class PersonCreateAuthorizer extends PersonReadAuthorizer {

  private static final String SESSION_NAME = "authorization-rules";
  private static final String CONFIGURATION_NAME = "authorization-rules";
  private static final String AGENDA_GROUP_NAME = "person-create-authorization-rules";

  public PersonCreateAuthorizer() {
    super(new DroolsConfiguration<>(
        SESSION_NAME,
        AGENDA_GROUP_NAME,
        CONFIGURATION_NAME));
  }
}

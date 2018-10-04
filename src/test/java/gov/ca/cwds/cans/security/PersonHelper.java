package gov.ca.cwds.cans.security;

import gov.ca.cwds.cans.domain.entity.County;
import gov.ca.cwds.cans.domain.entity.Person;

public class PersonHelper {

  private PersonHelper() {}

  public static Person getPerson(String countyExtId) {
    Person person = new Person();
    County county = new County();
    county.setExternalId(countyExtId);
    person.setCounty(county);
    return person;
  }
}

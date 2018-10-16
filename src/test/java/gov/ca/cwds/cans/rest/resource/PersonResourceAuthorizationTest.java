package gov.ca.cwds.cans.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.person.PersonDto;
import gov.ca.cwds.cans.test.util.FixtureReader;
import java.io.IOException;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersonResourceAuthorizationTest extends AbstractFunctionalTest {

  private static final String FIXTURES_POST_SENSITIVE = "fixtures/person-post-sensitive.json";
  private static final String FIXTURES_POST_SEALED =
      "fixtures/person-post-with-sensitivity-type.json";
  private static final String FIXTURES_POST_NON_SENSITIVE = "fixtures/person-post.json";

  private static final CountyDto EL_DORADO_COUNTY = new CountyDto();

  static {
    EL_DORADO_COUNTY.setId(9L);
    EL_DORADO_COUNTY.setName("El Dorado");
    EL_DORADO_COUNTY.setExportId("09");
    EL_DORADO_COUNTY.setExternalId("1076");
  }

  private PersonResourceHelper personHelper;

  @After
  public void tearDown() throws IOException {
    personHelper.cleanUp();
  }

  @Before
  public void before() {
    personHelper = new PersonResourceHelper(clientTestRule);
  }

  @Test
  public void personGet_Success_whenHasSensitivePrivilege() throws IOException {
    final PersonDto person =
        personHelper.postPerson(FixtureReader.readObject(FIXTURES_POST_SENSITIVE, PersonDto.class));
    Response response = personHelper.getPerson(AUTHORIZED_ACCOUNT_FIXTURE, person.getId());
    assertThat(response.getStatus(), is(HttpStatus.SC_OK));
    checkMetadataEditable(response, true);
  }

  @Test
  public void personGet_Unauthorized_whenNoSensitivePrivilege() throws IOException {
    final PersonDto person =
        personHelper.postPerson(FixtureReader.readObject(FIXTURES_POST_SENSITIVE, PersonDto.class));
    Response response =
        personHelper.getPerson(NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE, person.getId());
    assertThat(response.getStatus(), is(HttpStatus.SC_FORBIDDEN));
  }

  @Test
  public void personGet_Unauthorized_whenCountyIsNotTheSame() throws IOException {
    PersonDto person = personHelper.readPersonDto(FIXTURES_POST_SENSITIVE);
    person.setCounty(EL_DORADO_COUNTY);
    final PersonDto postedPerson =
        personHelper.postPerson(person, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    Response response = personHelper.getPerson(AUTHORIZED_ACCOUNT_FIXTURE, postedPerson.getId());
    assertThat(response.getStatus(), is(HttpStatus.SC_FORBIDDEN));
  }

  @Test
  public void personPut_Success_whenHasSensitivePrivilege() throws IOException {
    final PersonDto person =
        personHelper.postPerson(FixtureReader.readObject(FIXTURES_POST_SENSITIVE, PersonDto.class));
    person.setMiddleName("");
    Response response = personHelper.putPerson(AUTHORIZED_ACCOUNT_FIXTURE, person);
    assertThat(response.getStatus(), is(HttpStatus.SC_OK));
    checkMetadataEditable(response, true);
  }

  @Test
  public void personPut_Unauthorized_whenNoSensitivePrivilege() throws IOException {
    final PersonDto person =
        personHelper.postPerson(FixtureReader.readObject(FIXTURES_POST_SENSITIVE, PersonDto.class));
    person.setMiddleName("");
    Response response = personHelper.putPerson(NO_SEALED_NO_SENSITIVE_ACCOUNT_FIXTURE, person);
    assertThat(response.getStatus(), is(HttpStatus.SC_FORBIDDEN));
  }

  @Test
  public void personPut_Unauthorized_whenCountyIsNotTheSame() throws IOException {
    PersonDto person = personHelper.readPersonDto(FIXTURES_POST_SENSITIVE);
    final PersonDto postedPerson = personHelper.postPerson(person);
    Response response = personHelper.putPerson(AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE, postedPerson);
    assertThat(response.getStatus(), is(HttpStatus.SC_FORBIDDEN));
  }

  @Test
  public void personPost_Unauthorized_whenPersonIsSensitiveAndUserHasDifferentCounty()
      throws IOException {
    PersonDto person = personHelper.readPersonDto(FIXTURES_POST_SENSITIVE);
    final Response response =
        personHelper.postPersonAndGetResponse(person, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    assertThat(response.getStatus(), is(HttpStatus.SC_FORBIDDEN));
  }

  @Test
  public void personPost_Unauthorized_whenPersonIsSealedAndUserHasDifferentCounty()
      throws IOException {
    PersonDto person = personHelper.readPersonDto(FIXTURES_POST_SEALED);
    final Response response =
        personHelper.postPersonAndGetResponse(person, AUTHORIZED_ACCOUNT_FIXTURE);
    assertThat(response.getStatus(), is(HttpStatus.SC_FORBIDDEN));
  }

  @Test
  public void personPost_Authorized_whenPersonIsSealedAndUserHasSameCounty() throws IOException {
    PersonDto person = personHelper.readPersonDto(FIXTURES_POST_SEALED);
    final Response response =
        personHelper.postPersonAndGetResponse(person, AUTHORIZED_EL_DORADO_ACCOUNT_FIXTURE);
    assertThat(response.getStatus(), is(HttpStatus.SC_CREATED));
  }

  @Test
  public void personPost_Authorized_whenPersonIsSensitiveAndUserHasSameCounty() throws IOException {
    PersonDto person = personHelper.readPersonDto(FIXTURES_POST_SENSITIVE);
    final Response response =
        personHelper.postPersonAndGetResponse(person, AUTHORIZED_ACCOUNT_FIXTURE);
    assertThat(response.getStatus(), is(HttpStatus.SC_CREATED));
  }

  @Test
  public void personPost_Authorized_whenPersonHasNoSensitivityType() throws IOException {
    PersonDto person = personHelper.readPersonDto(FIXTURES_POST_NON_SENSITIVE);
    final Response response =
        personHelper.postPersonAndGetResponse(person, AUTHORIZED_ACCOUNT_FIXTURE);
    assertThat(response.getStatus(), is(HttpStatus.SC_CREATED));
  }
}

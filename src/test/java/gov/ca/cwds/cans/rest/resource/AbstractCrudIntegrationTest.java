package gov.ca.cwds.cans.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.cans.domain.dto.Dto;
import io.dropwizard.testing.FixtureHelpers;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;

/** @author denys.davydov */
public abstract class AbstractCrudIntegrationTest<T extends Dto> extends AbstractIntegrationTest {

  protected static final ObjectMapper OBJECT_MAPPER = ObjectMapperUtils.createObjectMapper();

  private Class<T> managedClass = this.getManagedClass();

  abstract String getApiPath();

  abstract String getPostFixturePath();

  abstract String getPutFixturePath();

  protected void assertPostGetPutDelete() throws IOException {
    // ========================= POST ========================
    // given
    final String fixturePost = FixtureHelpers.fixture(this.getPostFixturePath());
    final T dto = OBJECT_MAPPER.readValue(fixturePost, managedClass);

    // when + then
    final Long id = this.assertPostOperation(dto);

    // ========================= GET ========================
    // when
    final T getDtoResult =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(this.getApiPath() + SLASH + id)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get(managedClass);

    // then
    getDtoResult.setId(null);
    assertThat(getDtoResult, is(dto));

    // ========================= PUT ========================
    // given
    final String fixturePut = FixtureHelpers.fixture(this.getPutFixturePath());
    final T dto2 = OBJECT_MAPPER.readValue(fixturePut, managedClass);
    dto2.setId(id);

    // when + then
    this.assertPutOperation(dto2);

    // ========================= GET 2 ========================
    // when
    final T getDto2Result =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(this.getApiPath() + SLASH + id)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get(managedClass);

    // then
    assertThat(getDto2Result, is(dto2));

    // ======================== DELETE =======================
    // when
    clientTestRule
        .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
        .target(this.getApiPath() + SLASH + id)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .delete();

    final Response getDtoResult2 =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(this.getApiPath() + SLASH + id)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();

    // then
    assertThat(getDtoResult2.getStatus(), is(HttpStatus.SC_NOT_FOUND));
  }

  private Long assertPostOperation(T dto) throws IOException {
    // given
    final Entity<T> inputDto = Entity.entity(dto, MediaType.APPLICATION_JSON_TYPE);

    // when
    final Response postResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(this.getApiPath())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(inputDto);

    final T actualResult = postResponse.readEntity(managedClass);

    // then
    final Long id = actualResult.getId();
    assertThat(id, is(not(nullValue())));
    actualResult.setId(null);
    assertThat(actualResult, is(dto));
    return id;
  }

  private void assertPutOperation(T dto) throws IOException {
    // given
    final Entity<T> inputDto = Entity.entity(dto, MediaType.APPLICATION_JSON_TYPE);

    // when
    final Response putResponse =
        clientTestRule
            .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
            .target(this.getApiPath() + SLASH + dto.getId())
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(inputDto);

    final T actualResult = putResponse.readEntity(managedClass);

    // then
    assertThat(actualResult, is(dto));
  }

  private Class<T> getManagedClass() {
    ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
    Type[] actualTypeArguments = superclass.getActualTypeArguments();
    Type type = actualTypeArguments[0];
    return (Class<T>) type;
  }
}

package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.PEOPLE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.dto.PersonDto;
import gov.ca.cwds.cans.domain.dto.logging.CreationLoggable;
import gov.ca.cwds.cans.domain.dto.logging.UpdateLoggable;
import gov.ca.cwds.cans.test.util.FixtureReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;

/** @author denys.davydov */
public abstract class AbstractCrudFunctionalTest<T extends Dto> extends AbstractFunctionalTest {

  protected final Set<Long> createdUsersList = new HashSet<>();

  private Class<T> managedClass = this.getManagedClass();

  abstract String getApiPath();

  abstract String getPostFixturePath();

  abstract String getPutFixturePath();

  protected void assertPostGetPutDelete() throws IOException {
    // ========================= POST ========================
    // given
    final T dto = FixtureReader.readObject(this.getPostFixturePath(), managedClass);

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
    handleCreationLoggableInstance(getDtoResult);
    assertThat(getDtoResult, is(dto));

    // ========================= PUT ========================
    // given
    final T dto2 = FixtureReader.readObject(this.getPutFixturePath(), managedClass);
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
    handleCreationLoggableInstance(getDto2Result);
    handleUpdateLoggableInstance(getDto2Result);
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

    // clean up
    cleanUpCreatedUsers();
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
    handleCreationLoggableInstance(actualResult);
    assertThat(actualResult, is(dto));
    return id;
  }

  protected void handleCreationLoggableInstance(T actualResult) {
    if (actualResult instanceof CreationLoggable) {
      final CreationLoggable creationLoggable = (CreationLoggable) actualResult;
      final PersonDto createdBy = creationLoggable.getCreatedBy();
      createdUsersList.add(createdBy.getId());
      creationLoggable.setCreatedBy(null);
      creationLoggable.setCreatedTimestamp(null);
    }
  }

  protected void handleUpdateLoggableInstance(T actualResult) {
    if (actualResult instanceof UpdateLoggable) {
      final UpdateLoggable updateLoggable = (UpdateLoggable) actualResult;
      createdUsersList.add(updateLoggable.getUpdatedBy().getId());
      updateLoggable.setUpdatedBy(null);
      updateLoggable.setUpdatedTimestamp(null);
    }
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
    handleUpdateLoggableInstance(actualResult);
    assertThat(actualResult, is(dto));
  }

  private Class<T> getManagedClass() {
    ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
    Type[] actualTypeArguments = superclass.getActualTypeArguments();
    Type type = actualTypeArguments[0];
    return (Class<T>) type;
  }

  protected void cleanUpCreatedUsers() throws IOException {
    if (createdUsersList.isEmpty()) {
      return;
    }
    for (Long userId : createdUsersList) {
      clientTestRule
          .withSecurityToken(AUTHORIZED_ACCOUNT_FIXTURE)
          .target(PEOPLE + SLASH + userId)
          .request(MediaType.APPLICATION_JSON_TYPE)
          .delete();
    }
    createdUsersList.clear();
  }
}

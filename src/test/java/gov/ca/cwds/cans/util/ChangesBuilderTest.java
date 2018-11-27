package gov.ca.cwds.cans.util;

import gov.ca.cwds.cans.util.ChangesBuilder.BuilderError;
import java.util.LinkedList;
import java.util.List;
import liquibase.change.Change;
import liquibase.change.core.UpdateDataChange;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** @author CWDS TPT-2 Team */
public class ChangesBuilderTest {

  private ChangesBuilder builder;

  @Before
  public void before() {
    builder = new ChangesBuilder();
  }

  @Test
  public void buildChanges_success_withNoError() {
    List<Change> validChanges = new LinkedList<>();
    validChanges.add(new UpdateDataChange());
    builder.addChangesProvider(
        () -> {
          return validChanges;
        });
    builder.addValidator(change -> (null));
    List<Change> changes = builder.build();
    Assert.assertThat(changes.size(), Is.is(1));
    Assert.assertThat(builder.getErrors().size(), Is.is(0));
  }

  @Test
  public void buildChanges_success_withAnError() {
    List<Change> changes = new LinkedList<>();
    UpdateDataChange change1 = new UpdateDataChange();
    change1.setTableName("valid");
    changes.add(change1);
    UpdateDataChange change2 = new UpdateDataChange();
    change2.setTableName("invalid");
    changes.add(change2);
    builder.addChangesProvider(
        () -> {
          return changes;
        });
    builder.addValidator(
        change -> {
          if (!((UpdateDataChange) change).getTableName().equals("valid")) {
            return new BuilderError("Error", null);
          } else {
            return null;
          }
        });
    List<Change> builtChanges = builder.build();
    Assert.assertThat(builtChanges.size(), Is.is(1));
    Assert.assertThat(builder.getErrors().size(), Is.is(1));
  }

  @Test
  public void printErrorTest_success() {
    String errorMessage = "Error1";
    Throwable cause = new IllegalStateException();
    builder.addError(errorMessage, cause);
    builder.build();
    Assert.assertThat(builder.getErrors().size(), Is.is(1));
    BuilderError error = builder.getErrors().get(0);
    Assert.assertThat(error.getMessage(), Is.is(errorMessage));
    Assert.assertThat(error.getCause(), Is.is(cause));
    builder.printErrors();
  }
}

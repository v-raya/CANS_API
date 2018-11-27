package gov.ca.cwds.cans.util;

import com.google.common.collect.ImmutableList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import liquibase.change.Change;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** @author CWDS TPT-2 Team */
@Slf4j
public class ChangesBuilder {

  private final List<BuilderError> errors = new LinkedList<>();
  private final List<Change> changes = new LinkedList<>();
  private final Set<ChangesProvider> changesProviders = new HashSet<>();
  private final Set<ChangeValidator> validators = new HashSet<>();

  public ChangesBuilder addValidator(ChangeValidator validator) {
    validators.add(validator);
    return this;
  }

  public ChangesBuilder addChangesProvider(ChangesProvider provider) {
    changesProviders.add(provider);
    return this;
  }

  public ChangesBuilder addError(String message, Throwable cause) {
    errors.add(new BuilderError(message, cause));
    return this;
  }

  public List<Change> build() {
    changesProviders.forEach(changesProvider -> changes.addAll(changesProvider.getChanges()));
    List<Change> validChanges = changes.stream().filter(this::isValid).collect(Collectors.toList());
    return new ImmutableList.Builder<Change>().addAll(validChanges).build();
  }

  private boolean isValid(Change change) {
    final boolean[] result = {true};
    validators.forEach(
        validator ->
            result[0] &= // NOSONAR
                Optional.ofNullable(validator.validate(change))
                    .map(
                        error -> {
                          errors.add(error);
                          return Boolean.FALSE;
                        })
                    .orElse(Boolean.TRUE));
    return result[0];
  }

  public void printErrors() {
    log.warn("================ Errors ==================");
    errors.forEach(builderError -> log.warn("{}", builderError.message));
    log.warn("==========================================");
  }

  public List<BuilderError> getErrors() {
    return new LinkedList<>(errors);
  }

  @Getter
  @AllArgsConstructor
  public static class BuilderError {
    private String message;
    private Throwable cause;
  }
}

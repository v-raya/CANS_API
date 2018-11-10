package gov.ca.cwds.cans.domain.dto.changelog;

import gov.ca.cwds.cans.domain.entity.Persistent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Change log dto factory to build appripriate change log dto class instances
 *
 * @author CWDS API Team
 */
public class ChangeLogDtoFactory {

  // Restrict instantiating
  private ChangeLogDtoFactory() {}

  public static <E extends Persistent, D extends AbstractChangeLogDto> D newInstance(
      Class<D> dtoClass, ChangeLogDtoParameters<E> dtoParams) {

    if (dtoParams.getCurrent() == null) {
      return null;
    }

    Constructor<D> dtcoConstructor = getChangeLogDtoClassConstructor(dtoClass);

    try {
      return dtcoConstructor.newInstance(dtoParams);
    } catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {
      throw new IllegalArgumentException("Error creating ChangeLogDto Instance.", e);
    }
  }

  private static <D extends AbstractChangeLogDto> Constructor<D> getChangeLogDtoClassConstructor(
      final Class<D> dtoClass) {

    Class[] constructorArgs = new Class[1]; // Our changeLogDto constructors MUST have 1 arguments
    constructorArgs[0] =
        ChangeLogDtoParameters.class; // First argument is of *object* type ChangeLogDtoParameters

    try {
      return dtoClass.getDeclaredConstructor(constructorArgs);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(
          "The provided dtoClass:"
              + dtoClass.getName()
              + " doesn't have required 1 argument constructor");
    }
  }
}

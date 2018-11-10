package gov.ca.cwds.cans.domain.dto.changelog;

import gov.ca.cwds.cans.domain.entity.Persistent;
import gov.ca.cwds.cans.domain.entity.envers.NsRevisionEntity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.hibernate.envers.RevisionType;

/**
 * Change log dto factory to build appripriate change log dto class instances
 *
 * @author CWDS API Team
 */
public class ChangeLogDtoFactory {

  //Restrict instantiating
  private ChangeLogDtoFactory() {
  }

  public static <E extends Persistent, D extends AbstractChangeLogDto> D newInstance(
      Class<D> dtoClass, NsRevisionEntity revisionEntity, RevisionType revisionType, E currEntity,
      E prevEntity) {

    if (currEntity == null) {
      return null;
    }

    Constructor<D> dtcoConstructor = getChangeLogDtoClassConstructor(currEntity.getClass(),
        dtoClass);

    try {
      return dtcoConstructor.newInstance(revisionEntity, revisionType, currEntity, prevEntity);
    } catch (InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new IllegalArgumentException("Error creating ChangeLogDto Instance.", e);
    }
  }

  private static <E extends Persistent, D extends AbstractChangeLogDto> Constructor<D> getChangeLogDtoClassConstructor(
      final Class<E> entityClass, final Class<D> dtoClass) {

    Class[] constructorArgs = new Class[4];      //Our changeLogDto constructors MUST have 4 arguments
    constructorArgs[0] = NsRevisionEntity.class; //First argument is of *object* type NsRevisionEntity
    constructorArgs[1] = RevisionType.class;     //Second argument is of *object* type RevisionType
    constructorArgs[2] = entityClass;            //Third argument is of *object* type entityClass
    constructorArgs[3] = entityClass;            //Fours argument is of *object* type entityClass

    try {
      return dtoClass.getDeclaredConstructor(constructorArgs);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("The provided dtoClass:" + dtoClass.getName()
          + " doesn't have required 4 argument constructor");
    }
  }

}

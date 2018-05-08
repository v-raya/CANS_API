package gov.ca.cwds.cans.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.cans.domain.mapper.ConstructMapper;
import gov.ca.cwds.cans.domain.mapper.CountyMapper;
import org.mapstruct.factory.Mappers;

/**
 * DI (dependency injection) setup for mapping classes.
 *
 * @author denys.davydov
 */
public class MappingModule extends AbstractModule {

  @Override
  protected void configure() {
    bindMapperAsEagerSingleton(CountyMapper.class);
    bindMapperAsEagerSingleton(ConstructMapper.class);
  }

  private void bindMapperAsEagerSingleton(Class<?> clazz) {
    bind(clazz).to(getMapperImpl(clazz)).asEagerSingleton();
  }

  private static Class getMapperImpl(Class<?> mapperClass) {
    return Mappers.getMapper(mapperClass).getClass();
  }
}

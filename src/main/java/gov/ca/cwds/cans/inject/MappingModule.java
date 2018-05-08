package gov.ca.cwds.cans.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.cans.domain.mapper.AssessmentMapper;
import gov.ca.cwds.cans.domain.mapper.CftMapper;
import gov.ca.cwds.cans.domain.mapper.InstrumentMapper;
import gov.ca.cwds.cans.domain.mapper.CountyMapper;
import gov.ca.cwds.cans.domain.mapper.PersonMapper;
import org.mapstruct.factory.Mappers;

/**
 * DI (dependency injection) setup for mapping classes.
 *
 * @author denys.davydov
 */
public class MappingModule extends AbstractModule {

  @Override
  protected void configure() {
    bindMapperAsEagerSingleton(AssessmentMapper.class);
    bindMapperAsEagerSingleton(CftMapper.class);
    bindMapperAsEagerSingleton(InstrumentMapper.class);
    bindMapperAsEagerSingleton(CountyMapper.class);
    bindMapperAsEagerSingleton(PersonMapper.class);
  }

  private void bindMapperAsEagerSingleton(Class<?> clazz) {
    bind(clazz).to(getMapperImpl(clazz)).asEagerSingleton();
  }

  private static Class getMapperImpl(Class<?> mapperClass) {
    return Mappers.getMapper(mapperClass).getClass();
  }
}

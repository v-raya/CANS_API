package gov.ca.cwds.cans.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.cans.domain.mapper.AssessmentMapper;
import gov.ca.cwds.cans.domain.mapper.CaseMapper;
import gov.ca.cwds.cans.domain.mapper.ClientMapper;
import gov.ca.cwds.cans.domain.mapper.CountyMapper;
import gov.ca.cwds.cans.domain.mapper.I18nMapper;
import gov.ca.cwds.cans.domain.mapper.InstrumentMapper;
import gov.ca.cwds.cans.domain.mapper.PersonMapper;
import gov.ca.cwds.cans.domain.mapper.PersonShortMapper;
import gov.ca.cwds.cans.domain.mapper.StaffClientMapper;
import gov.ca.cwds.cans.domain.mapper.StaffPersonMapper;
import gov.ca.cwds.cans.domain.mapper.search.SearchAssessmentRequestMapper;
import gov.ca.cwds.cans.domain.mapper.search.SearchPersonRequestMapper;
import org.mapstruct.factory.Mappers;

/**
 * DI (dependency injection) setup for mapping classes.
 *
 * @author denys.davydov
 */
public class MappingModule extends AbstractModule {

  private static Class getMapperImpl(Class<?> mapperClass) {
    return Mappers.getMapper(mapperClass).getClass();
  }

  @Override
  protected void configure() {
    bindMapperAsEagerSingleton(AssessmentMapper.class);
    bindMapperAsEagerSingleton(CaseMapper.class);
    bindMapperAsEagerSingleton(CountyMapper.class);
    bindMapperAsEagerSingleton(I18nMapper.class);
    bindMapperAsEagerSingleton(InstrumentMapper.class);
    bindMapperAsEagerSingleton(PersonMapper.class);
    bindMapperAsEagerSingleton(PersonShortMapper.class);
    bindMapperAsEagerSingleton(ClientMapper.class);
    bindMapperAsEagerSingleton(StaffPersonMapper.class);

    bindMapperAsEagerSingleton(SearchAssessmentRequestMapper.class);
    bindMapperAsEagerSingleton(SearchPersonRequestMapper.class);
    bindMapperAsEagerSingleton(StaffClientMapper.class);
  }

  private void bindMapperAsEagerSingleton(Class<?> clazz) {
    bind(clazz).to(getMapperImpl(clazz)).asEagerSingleton();
  }
}

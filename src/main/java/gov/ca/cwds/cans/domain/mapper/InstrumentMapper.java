package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.InstrumentDto;
import gov.ca.cwds.cans.domain.entity.Instrument;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper(uses = CountyMapper.class)
public interface InstrumentMapper extends AMapper<Instrument, InstrumentDto> {}

package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.person.SearchPersonRequest;
import gov.ca.cwds.cans.domain.search.SearchPersonPo;
import org.mapstruct.Mapper;

/** @author denys.davydov */
@Mapper
public interface SearchPersonMapper extends SearchMapper<SearchPersonPo, SearchPersonRequest> {}

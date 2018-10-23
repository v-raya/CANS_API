package gov.ca.cwds.cans.domain.mapper;

import com.google.common.collect.ImmutableList;
import gov.ca.cwds.cans.domain.dto.CaseDto;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.person.ChildDto;
import gov.ca.cwds.data.legacy.cms.entity.Case;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.legacy.cms.entity.enums.DateOfBirthStatus;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** @author CWDS TPT-2 Team */
@Mapper

public interface ChildMapper {

  @Mapping(target = "firstName", source = "commonFirstName")
  @Mapping(target = "lastName", source = "commonLastName")
  @Mapping(target = "middleName", source = "commonMiddleName")
  @Mapping(target = "dob", source = "birthDate")
  @Mapping(target = "estimatedDob", ignore = true)
  @Mapping(target = "gender", ignore = true)
  ChildDto toDto(Client client);

  public default ChildDto toChildDto(
      Client client, Collection<CountyDto> counties, Collection<CaseDto> clientCases) {
    ChildDto childDto = toDto(client);
    if(DateOfBirthStatus.ESTIMATED.equals(client.getDateOfBirthStatus())) {
      childDto.setEstimatedDob(Boolean.TRUE);
    }
    childDto.setExternalId(CmsKeyIdGenerator.getUIIdentifierFromKey(client.getIdentifier()));
    childDto.setCounty(counties.iterator().next());
    childDto.setCounties(ImmutableList.copyOf(counties));
    childDto.setCases(ImmutableList.copyOf(clientCases));
    return childDto;
  }


  public default CaseDto toCaseDto(Case cmsCase) {
    CaseDto caseDto = new CaseDto();
    caseDto.setExternalId(CmsKeyIdGenerator.getUIIdentifierFromKey(cmsCase.getIdentifier()));
    return caseDto;
  }

  List<CaseDto> toCaseDtoList(Collection<Case> clientCases);
}

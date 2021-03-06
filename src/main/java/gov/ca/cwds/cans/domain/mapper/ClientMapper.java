package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.CaseDto;
import gov.ca.cwds.cans.domain.dto.CountyDto;
import gov.ca.cwds.cans.domain.dto.person.ClientDto;
import gov.ca.cwds.cans.domain.entity.Person;
import gov.ca.cwds.cans.domain.enumeration.PersonRole;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import gov.ca.cwds.data.legacy.cms.entity.Case;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.legacy.cms.entity.enums.DateOfBirthStatus;
import gov.ca.cwds.data.legacy.cms.entity.enums.Sensitivity;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/** @author CWDS TPT-2 Team */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

  @Mapping(target = "identifier", source = "identifier")
  @Mapping(target = "firstName", source = "commonFirstName")
  @Mapping(target = "lastName", source = "commonLastName")
  @Mapping(target = "middleName", source = "commonMiddleName")
  @Mapping(target = "suffix", source = "suffixTitleDescription")
  @Mapping(target = "dob", source = "birthDate")
  @Mapping(target = "gender", ignore = true)
  ClientDto toDto(Client client);

  default ClientDto toClientDto(Client client, CountyDto county) {
    ClientDto childDto = toDto(client);
    if (DateOfBirthStatus.ESTIMATED.equals(client.getDateOfBirthStatus())) {
      childDto.setEstimatedDob(Boolean.TRUE);
    }
    childDto.setExternalId(CmsKeyIdGenerator.getUIIdentifierFromKey(client.getIdentifier()));
    childDto.setCounty(county);
    childDto.setSensitivityType(toSensitivityType(client.getSensitivity()));
    return childDto;
  }

  default SensitivityType toSensitivityType(Sensitivity sensitivity) {
    switch (sensitivity) {
      case SENSITIVE:
        return SensitivityType.SENSITIVE;
      case SEALED:
        return SensitivityType.SEALED;
      default:
        return null;
    }
  }

  default CaseDto toCaseDto(Case cmsCase) {
    CaseDto caseDto = new CaseDto();
    caseDto.setExternalId(CmsKeyIdGenerator.getUIIdentifierFromKey(cmsCase.getIdentifier()));
    return caseDto;
  }

  void toDto(@MappingTarget ClientDto dto, Person person);

  @AfterMapping
  default void afterMapping(@MappingTarget ClientDto clientDto, Person person) {
    clientDto.setIdentifier(person.getExternalId());
    if (PersonRole.CLIENT.equals(person.getPersonRole())) {
      clientDto.setExternalId(CmsKeyIdGenerator.getUIIdentifierFromKey(person.getExternalId()));
    }
  }

  Person toPerson(ClientDto clientDto);

  @AfterMapping
  default void afterMapping(@MappingTarget Person person, ClientDto clientDto) {
    person.setExternalId(clientDto.getIdentifier());
    person.setSensitivityType(null);
  }
}

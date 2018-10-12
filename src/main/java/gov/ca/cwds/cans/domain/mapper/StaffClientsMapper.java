package gov.ca.cwds.cans.domain.mapper;

import gov.ca.cwds.cans.domain.dto.StaffClientDto;
import gov.ca.cwds.cans.domain.enumeration.SensitivityType;
import gov.ca.cwds.data.legacy.cms.entity.facade.ClientByStaff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
@FunctionalInterface
public interface StaffClientsMapper {

  @Mapping(source = "clientFirstName", target = "person.firstName")
  @Mapping(source = "clientMiddleName", target = "person.middleName")
  @Mapping(source = "clientLastName", target = "person.lastName")
  @Mapping(source = "clientNameSuffix", target = "person.suffix")
  @Mapping(source = "clientBirthDate", target = "person.dob")
  @Mapping(source = "clientIdentifier", target = "person.identifier")
  @Mapping(constant = "CLIENT", target = "person.personRole")
  @Mapping(constant = "IN_PROGRESS", target = "assessmentStatus")
  @Mapping(source = "casePlanReviewDueDate", target = "casePlanReviewDueDate")
  @Mapping(
      expression = "java(toSensitivityType(clientByStaff.getClientSensitivityType()))",
      target = "person.sensitivityType")
  StaffClientDto toDto(ClientByStaff entity);

  default SensitivityType toSensitivityType(String clientSensitivityType) {
    SensitivityType sensitivityType;
    switch (clientSensitivityType) {
      case "R":
        sensitivityType = SensitivityType.SEALED;
        break;
      case "S":
        sensitivityType = SensitivityType.SENSITIVE;
        break;
      default:
        sensitivityType = null;
        break;
    }
    return sensitivityType;
  }
}

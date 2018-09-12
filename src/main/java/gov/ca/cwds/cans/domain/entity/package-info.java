@TypeDefs(
  value = {
    @TypeDef(
      name = "AssessmentJsonType",
      typeClass = JsonType.class,
      parameters = {
        @Parameter(name = SQL_TYPE, value = SQLTypes.OTHER_TYPE_NAME),
        @Parameter(
          name = RETURNED_CLASS_NAME_PARAM,
          value = "gov.ca.cwds.cans.domain.json.AssessmentJson"
        )
      }
    ),
    @TypeDef(name = "PostgreSqlEnum", typeClass = PostgreSqlEnumType.class)
  }
)
package gov.ca.cwds.cans.domain.entity;

import static gov.ca.cwds.cans.Constants.RETURNED_CLASS_NAME_PARAM;
import static gov.ca.cwds.cans.Constants.SQL_TYPE;

import gov.ca.cwds.cans.dao.hibernate.PostgreSqlEnumType;
import gov.ca.cwds.cans.dao.hibernate.JsonType;
import gov.ca.cwds.cans.dao.hibernate.PostgreSqlEnumType;
import gov.ca.cwds.cans.dao.hibernate.SQLTypes;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

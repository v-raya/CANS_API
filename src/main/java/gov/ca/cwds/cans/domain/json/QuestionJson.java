package gov.ca.cwds.cans.domain.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gov.ca.cwds.cans.domain.enumeration.QuestionType;
import lombok.Data;

/** @author denys.davydov */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class QuestionJson implements Json {
  private String code;
  private Boolean required;
  private Boolean confidential;
  private QuestionType questionType;
  private Integer rating;
}

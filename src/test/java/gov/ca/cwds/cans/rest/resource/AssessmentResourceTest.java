package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.Constants.API.ASSESSMENTS;

import gov.ca.cwds.cans.domain.dto.AssessmentDto;
import java.io.IOException;
import org.junit.Test;

/** @author denys.davydov */
public class AssessmentResourceTest extends AbstractCrudIntegrationTest<AssessmentDto> {

  @Override
  String getPostFixturePath() {
    return "fixtures/assessment-post.json";
  }

  @Override
  String getPutFixturePath() {
    return "fixtures/assessment-put.json";
  }

  @Override
  String getApiPath() {
    return ASSESSMENTS;
  }

  @Test
  public void assessment_postGetPutDelete_success() throws IOException {
    this.assertPostGetPutDelete();
  }
}

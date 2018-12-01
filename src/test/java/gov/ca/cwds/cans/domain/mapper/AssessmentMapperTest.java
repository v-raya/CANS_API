package gov.ca.cwds.cans.domain.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentMetaDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

/** @author denys.davydov */
public class AssessmentMapperTest {

  private AssessmentMapper testSubject = Mappers.getMapper(AssessmentMapper.class);

  private static final String CASE_ID = "C6vN5DG0Aq";
  private static final String CASE_BASE10_KEY = "0687-9473-7673-8000672";

  @Test
  public void toShortDto_addsServiceSourceUiIdField_afterMapping() {
    final Assessment input = new Assessment().setServiceSourceId(CASE_ID);
    final AssessmentMetaDto actual = testSubject.toShortDto(input);
    assertThat(actual.getServiceSourceUiId(), is(CASE_BASE10_KEY));
  }

  @Test
  public void toShortDtos_addsServiceSourceUiIdField_afterMapping() {
    final Assessment input = new Assessment().setServiceSourceId(CASE_ID);
    final Collection<AssessmentMetaDto> actual =
        testSubject.toShortDtos(Collections.singletonList(input));
    assertThat(actual.iterator().next().getServiceSourceUiId(), is(CASE_BASE10_KEY));
  }

  @Test
  public void toDto_addsServiceSourceUiIdField_afterMapping() {
    final Assessment input = new Assessment().setServiceSourceId(CASE_ID);
    final AssessmentDto actual = testSubject.toDto(input);
    assertThat(actual.getServiceSourceUiId(), is(CASE_BASE10_KEY));
  }

  @Test
  public void toDtos_addsServiceSourceUiIdField_afterMapping() {
    final Assessment input = new Assessment().setServiceSourceId(CASE_ID);
    final Collection<AssessmentDto> actual =
        testSubject.toDtos(Collections.singletonList(input));
    assertThat(actual.iterator().next().getServiceSourceUiId(), is(CASE_BASE10_KEY));
  }
}

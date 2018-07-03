package gov.ca.cwds.cans.validation;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.json.AssessmentJson;
import gov.ca.cwds.cans.domain.json.DomainJson;
import gov.ca.cwds.cans.domain.json.ItemJson;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author denys.davydov
 */
public class ValidAssessmentValidator implements ConstraintValidator<ValidAssessment, Assessment> {

  @Override
  public void initialize(ValidAssessment constraintAnnotation) {
    // nothing to do
  }

  @Override
  @SuppressFBWarnings(
      value = "fb-contrib:SEO_SUBOPTIMAL_EXPRESSION_ORDER",
      justification = "No short circle applicable because we need all the violation, not the first one only"
  )
  public boolean isValid(final Assessment assessment, final ConstraintValidatorContext context) {
    if (assessment == null) {
      return true;
    }

    if (assessment.getStatus() != AssessmentStatus.SUBMITTED) {
      return true;
    }

    return isEventDateValid(assessment, context)
            & isAssessmentTypeValid(assessment, context)
            & isCompletedAsValid(assessment, context)
            & isCanReleaseConfidentialInfoValid(assessment, context)
            & isUnderSixValid(assessment, context)
            & areItemsValid(assessment, context);
  }

  private boolean isEventDateValid(final Assessment assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(
        assessment.getEventDate(),
        "Assessment Date",
        "event_date",
        context
    );
  }

  private boolean isAssessmentTypeValid(final Assessment assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(
        assessment.getAssessmentType(),
        "Assessment Type",
        "assessment_type",
        context
    );
  }

  private boolean isCompletedAsValid(final Assessment assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(
        assessment.getCompletedAs(),
        "Complete As",
        "completed_as",
        context
    );
  }

  private boolean isCanReleaseConfidentialInfoValid(final Assessment assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(
        assessment.getCanReleaseConfidentialInfo(),
        "Authorization for release of information on file",
        "can_release_confidential_info",
        context
    );
  }

  private boolean isUnderSixValid(final Assessment assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(
        assessment.getState().getUnderSix(),
        "Age Group",
        "state.is_under_six",
        context
    );
  }

  private boolean areItemsValid(final Assessment assessment, final ConstraintValidatorContext context) {
    final List<ItemJson> itemsWithNoRating = findItemsWithNoRating(assessment.getState());
    if (itemsWithNoRating.isEmpty()) {
      return true;
    }
    itemsWithNoRating.forEach(item -> context
        .buildConstraintViolationWithTemplate("The item has no rating")
        .addPropertyNode("item." + item.getCode())
        .addConstraintViolation()
        .disableDefaultConstraintViolation()
    );
    return false;
  }

  private List<ItemJson> findItemsWithNoRating(final AssessmentJson assessment) {
    final boolean isUnderSix = isTrue(assessment.getUnderSix());
    final List<ItemJson> allItemsByAgeGroup = assessment.getDomains().stream()
        .filter(d -> (isTrue(d.getUnderSix()) && isUnderSix) || (isTrue(d.getAboveSix()) && !isUnderSix))
        .map(domain -> ((DomainJson) domain).getItems())
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    return allItemsByAgeGroup.stream()
        .filter(item -> item.getRating() == -1)
        .collect(Collectors.toList());
  }

  private boolean isPropertyNotNull(final Object value, final String propertyName,
      final String propertyNode, final ConstraintValidatorContext context) {
    if (value == null) {
      context
          .buildConstraintViolationWithTemplate(String.format("The \'%s\' field cannot be empty", propertyName))
          .addPropertyNode(propertyNode)
          .addConstraintViolation()
          .disableDefaultConstraintViolation();
    }
    return value != null;
  }
}

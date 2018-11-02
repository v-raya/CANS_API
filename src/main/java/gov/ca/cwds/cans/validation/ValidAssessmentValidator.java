package gov.ca.cwds.cans.validation;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.inject.Key;
import gov.ca.cwds.cans.domain.dto.assessment.AssessmentDto;
import gov.ca.cwds.cans.domain.entity.Assessment;
import gov.ca.cwds.cans.domain.enumeration.AssessmentStatus;
import gov.ca.cwds.cans.domain.json.AssessmentJson;
import gov.ca.cwds.cans.domain.json.DomainJson;
import gov.ca.cwds.cans.domain.json.ItemJson;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.cans.inject.InjectorHolder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;

/**
 * @author denys.davydov
 */
public class ValidAssessmentValidator
    implements ConstraintValidator<ValidAssessment, AssessmentDto>, PersistentAware<Assessment> {

  @Override
  public void initialize(ValidAssessment constraintAnnotation) {
    // nothing to do
  }

  @Override
  @SuppressWarnings({
      "fb-contrib:SEO_SUBOPTIMAL_EXPRESSION_ORDER",
      "findbugs:NS_DANGEROUS_NON_SHORT_CIRCUIT",
      "squid:S2178"
  })
  // Justification: No short circle applicable because we need all the violations, not the first one
  // only
  public boolean isValid(final AssessmentDto assessment, final ConstraintValidatorContext context) {
    if (assessment == null) {
      return true;
    }

    if (assessment.getStatus() != AssessmentStatus.COMPLETED) {
      return true;
    }

    return isEventDateValid(assessment, context)
        & isAssessmentTypeValid(assessment, context)
        & isCompletedAsValid(assessment, context)
        & isCanReleaseConfidentialInfoValid(assessment, context)
        & hasCaregiver(assessment, context)
        & isUnderSixValid(assessment, context)
        & areItemsValid(assessment, context)
        & isConductedByValid(assessment, context);
  }

  private boolean isEventDateValid(
      final AssessmentDto assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(assessment.getEventDate(), "Assessment Date", "event_date", context);
  }

  private boolean isAssessmentTypeValid(
      final AssessmentDto assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(
        assessment.getAssessmentType(), "Assessment Type", "assessment_type", context);
  }

  private boolean isCompletedAsValid(
      final AssessmentDto assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(assessment.getCompletedAs(), "Complete As", "completed_as", context);
  }

  private boolean isCanReleaseConfidentialInfoValid(
      final AssessmentDto assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(
        assessment.getCanReleaseConfidentialInfo(),
        "Authorization for release of information on file",
        "can_release_confidential_info",
        context);
  }

  private boolean hasCaregiver(
      final AssessmentDto assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(
        assessment.getCanReleaseConfidentialInfo(),
        "Youth Has Caregiver",
        "has_caregiver",
        context);
  }

  private boolean isUnderSixValid(
      final AssessmentDto assessment, final ConstraintValidatorContext context) {
    return isPropertyNotNull(
        assessment.getState().getUnderSix(), "Age Group", "state.is_under_six", context);
  }

  private boolean areItemsValid(
      final AssessmentDto assessment, final ConstraintValidatorContext context) {
    final List<ItemJson> itemsWithNoRating = findItemsWithNoRating(assessment.getState());
    if (itemsWithNoRating.isEmpty()) {
      return true;
    }
    itemsWithNoRating.forEach(
        item ->
            context
                .buildConstraintViolationWithTemplate("The item has no rating")
                .addPropertyNode("item." + item.getCode())
                .addConstraintViolation()
                .disableDefaultConstraintViolation());
    return false;
  }

  private List<ItemJson> findItemsWithNoRating(final AssessmentJson assessment) {
    final boolean isUnderSix = isTrue(assessment.getUnderSix());
    final List<ItemJson> allItemsByAgeGroup =
        assessment
            .getDomains()
            .stream()
            .filter(isUnderSixDomainFilter(isUnderSix))
            .map(DomainJson::getItems)
            .flatMap(Collection::stream)
            .filter(isUnderSixItemFilter(isUnderSix))
            .collect(Collectors.toList());

    return allItemsByAgeGroup
        .stream()
        .filter(item -> item.getRating() == -1)
        .collect(Collectors.toList());
  }

  private Predicate<DomainJson> isUnderSixDomainFilter(boolean isUnderSix) {
    return d -> (isUnderSix && isTrue(d.getUnderSix())) || (!isUnderSix && isTrue(d.getAboveSix()));
  }

  private Predicate<ItemJson> isUnderSixItemFilter(boolean isUnderSix) {
    return item ->
        (isUnderSix && isNotBlank(item.getUnderSixId()))
            || (!isUnderSix && isNotBlank(item.getAboveSixId()));
  }

  private boolean isPropertyNotNull(
      final Object value,
      final String propertyName,
      final String propertyNode,
      final ConstraintValidatorContext context) {
    if (value == null) {
      context
          .buildConstraintViolationWithTemplate(
              String.format("The \'%s\' field cannot be empty", propertyName))
          .addPropertyNode(propertyNode)
          .addConstraintViolation()
          .disableDefaultConstraintViolation();
    }
    return value != null;
  }

  private boolean isConductedByValid(AssessmentDto dto, ConstraintValidatorContext context) {
    Long assessmentId = dto.getId();
    boolean valid = true;
    if (assessmentId != null) {
      valid = Optional.ofNullable(getPersisted(assessmentId)).map(persisted -> {
        if (AssessmentStatus.COMPLETED == persisted.getStatus()
            && !StringUtils.equals(persisted.getConductedBy(), dto.getConductedBy())) {
          context
              .buildConstraintViolationWithTemplate(
                  "The 'conductedBy' field can not be changed if an assessment is completed")
              .addPropertyNode("conductedBy")
              .addConstraintViolation()
              .disableDefaultConstraintViolation();
          return false;
        }
        return true;
      }).orElseThrow(
          () -> new IllegalArgumentException("Can't find assessment by Id: " + assessmentId));
    }
    return valid;
  }

  protected Assessment getPersisted(Long id) {
    SessionFactory sessionFactory = InjectorHolder.INSTANCE.getInjector().getInstance(
        Key.get(SessionFactory.class, CansSessionFactory.class));
    return getPersisted(sessionFactory, Assessment.class, id);
  }

}

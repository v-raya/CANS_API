package gov.ca.cwds.cans.domain.enumeration;

import static java.util.Arrays.asList;

import java.util.List;

/** The RatingType enumeration. */
public enum RatingType {
  REGULAR(asList(-1, 0, 1, 2, 3)),
  BOOLEAN(asList(-1, 0, 1));

  public static final Integer NOT_APPLICABLE_RATING_VALUE = 8;

  private final List<Integer> permittedRatings;

  RatingType(List<Integer> permittedRatings) {
    this.permittedRatings = permittedRatings;
  }

  public boolean isValidRating(final Integer rating) {
    return permittedRatings.contains(rating);
  }
}

package gov.ca.cwds.cans.cache;

import java.util.Arrays;

public class CacheKey {

  private final Object[] elements;
  private final int hashCode;

  public CacheKey(Object... params) {
    this.elements = new Object[params.length];
    System.arraycopy(params, 0, this.elements, 0, params.length);
    this.hashCode = Arrays.deepHashCode(this.elements);
  }

  @Override
  public boolean equals(Object o) {
    return this == o
        || (o instanceof CacheKey && Arrays.deepEquals(this.elements, ((CacheKey) o).elements));
  }

  @Override
  public final int hashCode() {
    return this.hashCode;
  }
}

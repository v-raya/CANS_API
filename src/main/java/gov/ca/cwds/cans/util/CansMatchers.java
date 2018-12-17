package gov.ca.cwds.cans.util;

import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import java.lang.reflect.Method;

public class CansMatchers {

  private CansMatchers() {}

  public static Matcher<Method> methodByName(String name) {
    return new AbstractMatcher<Method>() {
      @Override
      public boolean matches(Method method) {
        return method.getName().equals(name);
      }
    };
  }
}

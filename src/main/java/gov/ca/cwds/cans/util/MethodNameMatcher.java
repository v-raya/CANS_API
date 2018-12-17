package gov.ca.cwds.cans.util;

import com.google.inject.matcher.AbstractMatcher;
import java.lang.reflect.Method;

public class MethodNameMatcher extends AbstractMatcher<Method> {

  private final String methodName;

  public MethodNameMatcher(String methodName) {
    this.methodName = methodName;
  }

  @Override
  public boolean matches(Method method) {
    return method.getName().equals(methodName);
  }
}

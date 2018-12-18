package gov.ca.cwds.cans.cache;

import org.aopalliance.intercept.MethodInvocation;

public class MethodKeyGenerator implements KeyGenerator {

  @Override
  public CacheKey generate(MethodInvocation invocation) {
    Object[] params = new Object[invocation.getArguments().length + 1];
    params[0] = invocation.getMethod().getName();
    System.arraycopy(invocation.getArguments(), 0, params, 1, invocation.getArguments().length);
    return new CacheKey(params);
  }
}

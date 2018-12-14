package gov.ca.cwds.cans.cache;

import org.aopalliance.intercept.MethodInvocation;

public interface KeyGenerator {

  CacheKey generate(MethodInvocation invocation);

}

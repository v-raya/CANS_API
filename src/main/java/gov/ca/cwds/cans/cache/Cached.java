package gov.ca.cwds.cans.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {

  String value() default "";

  Class<? extends KeyGenerator> generator() default MethodKeyGenerator.class;
}

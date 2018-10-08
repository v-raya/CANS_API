package gov.ca.cwds.cans.security;

import com.google.inject.Inject;
import gov.ca.cwds.drools.DroolsConfiguration;
import gov.ca.cwds.drools.DroolsService;
import gov.ca.cwds.security.authorizer.BaseAuthorizer;
import gov.ca.cwds.security.utils.PrincipalUtils;
import java.util.Arrays;

@SuppressWarnings({
  "squid:S00119"
}) // Having these 2 generic names not in line with the rule makes a code more readable
public class DroolsAuthorizer<Type, ID> extends BaseAuthorizer<Type, ID> {

  @Inject private DroolsService droolsService;
  private DroolsConfiguration<Type> configuration;

  public DroolsAuthorizer(DroolsConfiguration<Type> configuration) {
    this.configuration = configuration;
  }

  @Override
  protected boolean checkInstance(Type instance) {
    return droolsService.performAuthorizationRules(
        configuration, Arrays.asList(instance, PrincipalUtils.getPrincipal()));
  }
}

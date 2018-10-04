package gov.ca.cwds.cans.test.util;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.internal.ProviderMethodsModule;
import com.google.inject.util.Modules;
import gov.ca.cwds.cans.inject.CansSessionFactory;
import gov.ca.cwds.security.realm.PerryAccount;
import java.io.IOException;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.apache.shiro.util.ThreadContext;
import org.hibernate.SessionFactory;
import org.junit.Before;

public abstract class BaseUnitTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Before
  public void createTest() {
    Module module = ProviderMethodsModule.forObject(this);
    Module testModule =
        Modules.override(module)
            .with(
                new AbstractModule() {

                  @Override
                  protected void configure() {
                    bind(SessionFactory.class)
                        .annotatedWith(CansSessionFactory.class)
                        .toInstance(mock(SessionFactory.class));
                  }
                });
    Guice.createInjector(testModule).injectMembers(this);
  }

  protected void securityContext(String fixture) throws IOException {
    PerryAccount perryAccount = objectMapper.readValue(fixture(fixture), PerryAccount.class);
    PrincipalCollection collection = new SimplePrincipalCollection(perryAccount, "test");
    Subject subject =
        new DelegatingSubject(new DefaultSecurityManager()) {
          @Override
          public PrincipalCollection getPrincipals() {
            return collection;
          }
        };
    ThreadContext.bind(subject);
  }
}

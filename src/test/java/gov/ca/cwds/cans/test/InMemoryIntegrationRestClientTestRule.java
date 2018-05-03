package gov.ca.cwds.cans.test;

import gov.ca.cwds.cans.CansConfiguration;
import gov.ca.cwds.security.jwt.JwtConfiguration;
import gov.ca.cwds.security.jwt.JwtService;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author denys.davydov
 */
public class InMemoryIntegrationRestClientTestRule extends AbstractRestClientTestRule {

  private JwtConfiguration jwtConfiguration;

  public InMemoryIntegrationRestClientTestRule(DropwizardAppRule<CansConfiguration> dropWizardApplication) {
    token = initToken();
    mapper = dropWizardApplication.getObjectMapper();
    apiUrl = String.format("http://localhost:%s/", dropWizardApplication.getLocalPort());
  }

  @Override
  String generateToken(String identity, String password) throws IOException {
    JwtConfiguration configuration = getJwtConfiguration();
    JwtService jwtService = new JwtService(configuration);
    return jwtService.generate("id", "subject", identity);
  }

  private JwtConfiguration getJwtConfiguration() throws IOException {
    if (jwtConfiguration != null) {
      return jwtConfiguration;
    }

    Properties properties = new Properties();
    properties.load(new FileInputStream("config/shiro.ini"));

    jwtConfiguration = new JwtConfiguration();
    //JWT
    jwtConfiguration.setTimeout(30);
    jwtConfiguration.setIssuer(properties.getProperty("perryRealm.tokenIssuer"));
    jwtConfiguration.setKeyStore(new JwtConfiguration.KeyStoreConfiguration());
    //KeyStore
    jwtConfiguration.getKeyStore()
        .setPath(new File(properties.getProperty("perryRealm.keyStorePath")).getPath());
    jwtConfiguration.getKeyStore().setPassword(properties.getProperty("perryRealm.keyStorePassword"));
    //Sign/Validate Key
    jwtConfiguration.getKeyStore().setAlias(properties.getProperty("perryRealm.keyStoreAlias"));
    jwtConfiguration.getKeyStore()
        .setKeyPassword(properties.getProperty("perryRealm.keyStoreKeyPassword"));
    //Enc Key
    jwtConfiguration
        .setEncryptionEnabled(Boolean.valueOf(properties.getProperty("perryRealm.useEncryption")));
    jwtConfiguration.getKeyStore()
        .setEncKeyPassword(properties.getProperty("perryRealm.encKeyPassword"));
    jwtConfiguration.getKeyStore().setEncAlias(properties.getProperty("perryRealm.encKeyAlias"));
    jwtConfiguration.setEncryptionMethod(properties.getProperty("perryRealm.encryptionMethod"));
    return jwtConfiguration;
  }
}

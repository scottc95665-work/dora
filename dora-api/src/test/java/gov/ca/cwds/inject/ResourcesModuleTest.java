package gov.ca.cwds.inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import gov.ca.cwds.rest.DoraConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.SwaggerConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author TPT-2
 */
public class ResourcesModuleTest {

  private static final String APP_NAME = "AppName";

  private ResourcesModule module;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() {
    module = new ResourcesModule();
  }

  @Test
  public void testThatResourcesModuleIsCreatedWithDefaultConstructor() {
    assertNotNull(module);
  }

  @Test
  public void testConfigureFail() {
    thrown.expect(IllegalStateException.class);
    module.configure();
  }

  @Test
  public void testSwaggerConfigurationNPE() {
    thrown.expect(NullPointerException.class);
    module.swaggerConfiguration(null);
  }

  @Test
  public void testSwaggerConfiguration() {
    SwaggerConfiguration testSwaggerConfiguration = new SwaggerConfiguration();
    DoraConfiguration testDoraConfiguration = new DoraConfiguration();
    testDoraConfiguration.setSwaggerConfiguration(testSwaggerConfiguration);
    SwaggerConfiguration swaggerConfiguration = module.swaggerConfiguration(testDoraConfiguration);
    assertNotNull(swaggerConfiguration);
    assertEquals(testSwaggerConfiguration, swaggerConfiguration);
  }

  @Test
  public void testElasticSearchConfigurationNPE() {
    thrown.expect(NullPointerException.class);
    module.elasticSearchConfig(null);
  }

  @Test
  public void testElasticSearchConfiguration() {
    ElasticsearchConfiguration testElasticsearchConfiguration = new ElasticsearchConfiguration();
    DoraConfiguration testDoraConfiguration = new DoraConfiguration();
    testDoraConfiguration.setElasticsearchConfiguration(testElasticsearchConfiguration);
    ElasticsearchConfiguration elasticsearchConfiguration = module
        .elasticSearchConfig(testDoraConfiguration);
    assertNotNull(elasticsearchConfiguration);
    assertEquals(testElasticsearchConfiguration, elasticsearchConfiguration);
  }

  @Test
  public void testAppName() {
    DoraConfiguration testDoraConfiguration = new DoraConfiguration();
    String appName = module.provideAppName(testDoraConfiguration);
    assertNull(appName);

    testDoraConfiguration.setApplicationName(APP_NAME);
    appName = module.provideAppName(testDoraConfiguration);
    assertNotNull(appName);
    assertEquals(APP_NAME, appName);
  }

  @Test
  public void testAppVersion() {
    String version = module.provideAppVersion();
    assertNotNull(version);
  }
}

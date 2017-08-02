package gov.ca.cwds.inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import gov.ca.cwds.rest.DoraConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.SwaggerConfiguration;
import gov.ca.cwds.rest.resources.ApplicationResource;
import gov.ca.cwds.rest.resources.SwaggerResource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author TPT-2
 */
public class ResourcesModuleTest {

  private static final String APP_VERSION = "TestVersion";
  private static final String APP_NAME = "AppName";

  private ResourcesModule module;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() throws Exception {
    module = new ResourcesModule();
  }

  @Test
  public void testThatResourcesModuleIsCreatedWithDefaultConstructor() throws Exception {
    assertNotNull(module);
  }

  @Test
  public void testConfigureFail() throws Exception {
    thrown.expect(IllegalStateException.class);
    module.configure();
  }

  @Test
  public void testConfigure() throws Exception {
    Injector injector = Guice.createInjector(module);
    ResourcesModule resourcesModule = injector.getInstance(ResourcesModule.class);
    assertNotNull(resourcesModule);

    thrown.expect(ProvisionException.class);
    //should throw ProvisionException because appName and appVersion are not @Nullable
    ApplicationResource applicationResource = injector.getInstance(ApplicationResource.class);
    SwaggerResource swaggerResource = injector.getInstance(SwaggerResource.class);
  }

  @Test
  public void testSwaggerConfigurationNPE() throws Exception {
    thrown.expect(NullPointerException.class);
    SwaggerConfiguration swaggerConfiguration = module.swaggerConfiguration(null);
  }

  @Test
  public void testSwaggerConfiguration() throws Exception {
    SwaggerConfiguration testSwaggerConfiguration = new SwaggerConfiguration();
    DoraConfiguration testDoraConfiguration = new DoraConfiguration();
    testDoraConfiguration.setSwaggerConfiguration(testSwaggerConfiguration);
    SwaggerConfiguration swaggerConfiguration = module.swaggerConfiguration(testDoraConfiguration);
    assertNotNull(swaggerConfiguration);
    assertEquals(testSwaggerConfiguration, swaggerConfiguration);
  }

  @Test
  public void testElasticSearchConfigurationNPE() throws Exception {
    thrown.expect(NullPointerException.class);
    ElasticsearchConfiguration elasticsearchConfiguration = module.elasticSearchConfig(null);
  }

  @Test
  public void testElasticSearchConfiguration() throws Exception {
    ElasticsearchConfiguration testElasticsearchConfiguration = new ElasticsearchConfiguration();
    DoraConfiguration testDoraConfiguration = new DoraConfiguration();
    testDoraConfiguration.setElasticsearchConfiguration(testElasticsearchConfiguration);
    ElasticsearchConfiguration elasticsearchConfiguration = module
        .elasticSearchConfig(testDoraConfiguration);
    assertNotNull(elasticsearchConfiguration);
    assertEquals(testElasticsearchConfiguration, elasticsearchConfiguration);
  }

  @Test
  public void testAppName() throws Exception {
    DoraConfiguration testDoraConfiguration = new DoraConfiguration();
    String appName = module.appName(testDoraConfiguration);
    assertNull(appName);

    testDoraConfiguration.setApplicationName(APP_NAME);
    appName = module.appName(testDoraConfiguration);
    assertNotNull(appName);
    assertEquals(APP_NAME, appName);
  }

  @Test
  public void testAppVersion() throws Exception {
    DoraConfiguration testDoraConfiguration = new DoraConfiguration();
    String version = module.appVersion(testDoraConfiguration);
    assertNull(version);

    testDoraConfiguration.setVersion(APP_VERSION);
    version = module.appVersion(testDoraConfiguration);
    assertNotNull(version);
    assertEquals(APP_VERSION, version);
  }
}

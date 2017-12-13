package gov.ca.cwds.dora.health;

import static gov.ca.cwds.dora.health.BasicDoraHealthCheck.HEALTHY_ES_CONFIG_MSG;
import static gov.ca.cwds.dora.health.BasicDoraHealthCheck.UNHEALTHY_ES_CONFIG_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchHealthCheck.HEALTHY_ELASTICSEARCH_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchHealthCheck.UNHEALTHY_ELASTICSEARCH_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchIndexHealthCheck.ES_INDEXES_ENDPOINT;
import static gov.ca.cwds.dora.health.ElasticsearchIndexHealthCheck.HEALTHY_ES_INDEX_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchIndexHealthCheck.UNHEALTHY_ES_INDEX_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchPluginHealthCheck.ES_PLUGINS_ENDPOINT;
import static gov.ca.cwds.dora.health.ElasticsearchPluginHealthCheck.HEALTHY_ES_PLUGIN_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchPluginHealthCheck.UNHEALTHY_ES_PLUGIN_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchRolesHealthCheck.ES_ROLES_ENDPOINT;
import static gov.ca.cwds.dora.health.ElasticsearchRolesHealthCheck.HEALTHY_ES_ROLES_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchRolesHealthCheck.UNHEALTHY_ES_ROLES_MSG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.codahale.metrics.health.HealthCheck.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration.XpackConfiguration;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author CWDS TPT-2
 */
public class DoraHealthChecksTest {

  private static final ElasticsearchConfiguration ES_CONFIG_OK_NO_XPACK = esConfig("localhost",
      "9200", false, null, null);
  private static final ElasticsearchConfiguration ES_CONFIG_OK_XPACK = esConfig("dora.dev.cwds.io", "9200",
      true, "user", "password");

  @Test
  public void testElasticsearchConfigurations() throws Exception {
    assertIncorrectElasticsearchConfiguration(new ElasticsearchConfiguration("localhost", null));
    assertIncorrectElasticsearchConfiguration(new ElasticsearchConfiguration(null, "9200"));
    assertIncorrectElasticsearchConfiguration(esConfig("localhost", "9200", true, null, null));
    assertIncorrectElasticsearchConfiguration(esConfig("localhost", "9200", true, "user", null));
    assertIncorrectElasticsearchConfiguration(
        esConfig("localhost", "9200", true, null, "password"));

    assertCorrectElasticsearchConfiguration(ES_CONFIG_OK_NO_XPACK);
    assertCorrectElasticsearchConfiguration(ES_CONFIG_OK_XPACK);
  }

  @Test
  public void testElasticsearchUnavailable() throws Exception {
    Result result = new ElasticsearchHealthCheck(esConfig("localhost", "9999", false, null, null))
        .check();
    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertTrue(result.getMessage().startsWith(UNHEALTHY_ELASTICSEARCH_MSG));
  }

  @Test
  public void testElasticsearchAvailable() throws Exception {
    ElasticsearchHealthCheck elasticsearchHealthCheck = spy(
        new ElasticsearchHealthCheck(ES_CONFIG_OK_NO_XPACK));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    Result result = elasticsearchHealthCheck.check();

    assertNotNull(result);
    assertTrue(result.isHealthy());
    assertEquals(String.format(HEALTHY_ELASTICSEARCH_MSG, "5.5.0", "my_es_cluster"),
        result.getMessage());
  }

  @Test
  public void testElasticsearchPluginUnavailable() throws Exception {
    ElasticsearchPluginHealthCheck elasticsearchPluginHealthCheck = spy(
        new ElasticsearchPluginHealthCheck(ES_CONFIG_OK_XPACK, "x-pack"));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchPluginHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponse("/es/mock-responses/es-get-nodes-plugins-1.json"))
        .when(elasticsearchPluginHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq(ES_PLUGINS_ENDPOINT));
    Result result = elasticsearchPluginHealthCheck.check();

    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(String.format(UNHEALTHY_ES_PLUGIN_MSG, "x-pack", "lXdXDBl8QM6D6QXiPcTPMw"),
        result.getMessage());
  }

  @Test
  public void testElasticsearchPluginAvailable() throws Exception {
    ElasticsearchPluginHealthCheck elasticsearchPluginHealthCheck = spy(
        new ElasticsearchPluginHealthCheck(ES_CONFIG_OK_XPACK, "x-pack"));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchPluginHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponse("/es/mock-responses/es-get-nodes-plugins-2.json"))
        .when(elasticsearchPluginHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq(ES_PLUGINS_ENDPOINT));
    Result result = elasticsearchPluginHealthCheck.check();

    assertNotNull(result);
    assertTrue(result.isHealthy());
    assertEquals(String.format(HEALTHY_ES_PLUGIN_MSG, "x-pack"),result.getMessage());
  }

  @Test
  public void testElasticsearchIndexAvailable() throws Exception {
    ElasticsearchIndexHealthCheck elasticsearchIndexHealthCheck = spy(
        new ElasticsearchIndexHealthCheck(ES_CONFIG_OK_XPACK, "people"));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchIndexHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponseList("/es/mock-responses/es-get-indexes-1.json")).when(elasticsearchIndexHealthCheck)
        .performRequestList(Mockito.any(), eq("GET"), eq(ES_INDEXES_ENDPOINT));
    Result result = elasticsearchIndexHealthCheck.check();

    assertNotNull(result);
    assertTrue(result.isHealthy());
    assertEquals(String.format(HEALTHY_ES_INDEX_MSG, "people"),result.getMessage());
  }

  @Test
  public void testElasticsearchIndexNotAvailable() throws Exception {
    ElasticsearchIndexHealthCheck elasticsearchIndexHealthCheck = spy(
        new ElasticsearchIndexHealthCheck(ES_CONFIG_OK_XPACK, "people"));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchIndexHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponseList("/es/mock-responses/es-get-indexes-2.json")).when(elasticsearchIndexHealthCheck)
        .performRequestList(Mockito.any(), eq("GET"), eq(ES_INDEXES_ENDPOINT));
    Result result = elasticsearchIndexHealthCheck.check();

    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(String.format(UNHEALTHY_ES_INDEX_MSG, "people"),result.getMessage());
  }

  @Test
  public void testElasticsearchRoleAvailable() throws Exception {
    ElasticsearchRolesHealthCheck elasticsearchRolesHealthCheck = spy(
        new ElasticsearchRolesHealthCheck(ES_CONFIG_OK_XPACK, "worker"));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchRolesHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponse("/es/mock-responses/es-get-roles-1.json")).when(elasticsearchRolesHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq(ES_ROLES_ENDPOINT));
    Result result = elasticsearchRolesHealthCheck.check();

    assertNotNull(result);
    assertTrue(result.isHealthy());
    assertEquals(String.format(HEALTHY_ES_ROLES_MSG, "worker"),result.getMessage());
  }

  @Test
  public void testElasticsearchRoleNotAvailable() throws Exception {
    ElasticsearchRolesHealthCheck elasticsearchRolesHealthCheck = spy(
        new ElasticsearchRolesHealthCheck(ES_CONFIG_OK_XPACK, "worker"));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchRolesHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponse("/es/mock-responses/es-get-roles-2.json")).when(elasticsearchRolesHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq(ES_ROLES_ENDPOINT));
    Result result = elasticsearchRolesHealthCheck.check();

    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(String.format(UNHEALTHY_ES_ROLES_MSG, "worker"),result.getMessage());
  }

  private void assertIncorrectElasticsearchConfiguration(ElasticsearchConfiguration esConfig)
      throws Exception {
    Result result = new BasicDoraHealthCheck(esConfig).check();
    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(UNHEALTHY_ES_CONFIG_MSG, result.getMessage());
  }

  private void assertCorrectElasticsearchConfiguration(ElasticsearchConfiguration esConfig)
      throws Exception {
    Result result = new BasicDoraHealthCheck(esConfig).check();
    assertNotNull(result);
    assertTrue(result.isHealthy());
    assertEquals(String.format(HEALTHY_ES_CONFIG_MSG, esConfig.getHost(), esConfig.getPort(),
        esConfig.getXpack().isEnabled() ? "enabled" : "disabled"),
        result.getMessage());
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> loadMockResponse(String resourceFile) throws IOException {
    return new ObjectMapper()
        .readValue(DoraHealthChecksTest.class.getResourceAsStream(resourceFile), Map.class);
  }

  @SuppressWarnings("unchecked")
  private static List<Object> loadMockResponseList(String resourceFile) throws IOException {
    return new ObjectMapper()
        .readValue(DoraHealthChecksTest.class.getResourceAsStream(resourceFile), List.class);
  }

  private static ElasticsearchConfiguration esConfig(String host, String port, boolean xPackEnabled,
      String user, String password) {
    XpackConfiguration xpackConfig = new XpackConfiguration();
    xpackConfig.setEnabled(xPackEnabled);
    xpackConfig.setUser(user);
    xpackConfig.setPassword(password);

    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration(host, port);
    esConfig.setXpack(xpackConfig);

    return esConfig;
  }
}

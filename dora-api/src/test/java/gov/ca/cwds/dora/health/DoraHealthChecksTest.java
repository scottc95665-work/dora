package gov.ca.cwds.dora.health;

import static gov.ca.cwds.dora.health.BasicDoraHealthCheck.HEALTHY_ES_CONFIG_MSG;
import static gov.ca.cwds.dora.health.BasicDoraHealthCheck.UNHEALTHY_ES_CONFIG_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchHealthCheck.HEALTHY_ELASTICSEARCH_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchHealthCheck.UNHEALTHY_ELASTICSEARCH_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchIndexHealthCheck.ES_ALIASES_ENDPOINT;
import static gov.ca.cwds.dora.health.ElasticsearchIndexHealthCheck.ES_INDEXES_ENDPOINT;
import static gov.ca.cwds.dora.health.ElasticsearchIndexHealthCheck.HEALTHY_ES_INDEX_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchIndexHealthCheck.UNHEALTHY_ES_INDEX_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchPluginHealthCheck.ES_PLUGINS_ENDPOINT;
import static gov.ca.cwds.dora.health.ElasticsearchPluginHealthCheck.HEALTHY_ES_PLUGIN_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchPluginHealthCheck.UNHEALTHY_ES_PLUGIN_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchRolesHealthCheck.ES_ROLES_ENDPOINT;
import static gov.ca.cwds.dora.health.ElasticsearchRolesHealthCheck.HEALTHY_ES_ROLES_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchRolesHealthCheck.UNHEALTHY_ES_ROLES_MSG;
import static gov.ca.cwds.rest.BaseDoraApplicationTest.config;
import static gov.ca.cwds.rest.BaseDoraApplicationTest.esConfig;
import static gov.ca.cwds.rest.DoraConstants.DEV_MODE;
import static gov.ca.cwds.rest.DoraConstants.PROD_MODE;
import static gov.ca.cwds.rest.DoraConstants.Plugin.X_PACK_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.Role.WORKER_ROLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import gov.ca.cwds.rest.DoraConfiguration;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import com.codahale.metrics.health.HealthCheck.Result;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.rest.ElasticsearchConfiguration;

/**
 * @author CWDS TPT-2
 */
public class DoraHealthChecksTest {

  private static final DoraConfiguration CONFIG_OK_NO_XPACK =
      config(DEV_MODE, esConfig("localhost:9200", false, "user", "password"));
  private static final DoraConfiguration CONFIG_OK_XPACK =
      config(PROD_MODE, esConfig("dora.dev.cwds.io:9200", true, "user", "password"));

  private static final String PEOPLE_ALIAS = "people";

  @Test
  public void testElasticsearchConfigurations() throws Exception {
    new ElasticsearchConfiguration().setNodes("localhost:");
    assertIncorrectElasticsearchConfiguration(config("localhost:", false, null, null));
    assertIncorrectElasticsearchConfiguration(config(":9200", false, null, null));
    assertIncorrectElasticsearchConfiguration(config("localhost:9200", true, null, null));
    assertIncorrectElasticsearchConfiguration(config("localhost:9200", true, "user", null));
    assertIncorrectElasticsearchConfiguration(config("localhost:9200", true, null, "password"));

    assertCorrectElasticsearchConfiguration(CONFIG_OK_NO_XPACK);
    assertCorrectElasticsearchConfiguration(CONFIG_OK_XPACK);
  }

  @Test
  public void testElasticsearchUnavailable() throws Exception {
    ElasticsearchHealthCheck elasticsearchHealthCheck =
        spy(new ElasticsearchHealthCheck(config("localhost:9999", false, "user", "password")));
    doThrow(new IOException("")).when(elasticsearchHealthCheck).performRequest(Mockito.any(),
        eq("GET"), eq("/"));
    Result result = elasticsearchHealthCheck.check();

    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertTrue(result.getMessage().startsWith(UNHEALTHY_ELASTICSEARCH_MSG));
  }

  @Test
  public void testElasticsearchAvailable() throws Exception {
    ElasticsearchHealthCheck elasticsearchHealthCheck =
        spy(new ElasticsearchHealthCheck(CONFIG_OK_NO_XPACK));
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
    ElasticsearchPluginHealthCheck elasticsearchPluginHealthCheck =
        spy(new ElasticsearchPluginHealthCheck(CONFIG_OK_XPACK, X_PACK_PLUGIN));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json"))
        .when(elasticsearchPluginHealthCheck).performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponse("/es/mock-responses/es-get-nodes-plugins-1.json"))
        .when(elasticsearchPluginHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq(ES_PLUGINS_ENDPOINT));
    Result result = elasticsearchPluginHealthCheck.check();

    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(String.format(UNHEALTHY_ES_PLUGIN_MSG, X_PACK_PLUGIN, "lXdXDBl8QM6D6QXiPcTPMw"),
        result.getMessage());
  }

  @Test
  public void testElasticsearchPluginAvailable() throws Exception {
    ElasticsearchPluginHealthCheck elasticsearchPluginHealthCheck =
        spy(new ElasticsearchPluginHealthCheck(CONFIG_OK_XPACK, X_PACK_PLUGIN));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json"))
        .when(elasticsearchPluginHealthCheck).performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponse("/es/mock-responses/es-get-nodes-plugins-2.json"))
        .when(elasticsearchPluginHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq(ES_PLUGINS_ENDPOINT));
    Result result = elasticsearchPluginHealthCheck.check();

    assertNotNull(result);
    assertTrue(result.isHealthy());
    assertEquals(String.format(HEALTHY_ES_PLUGIN_MSG, X_PACK_PLUGIN), result.getMessage());
  }

  @Test
  public void testElasticsearchIndexAvailable() throws Exception {
    ElasticsearchIndexHealthCheck elasticsearchIndexHealthCheck =
        spy(new ElasticsearchIndexHealthCheck(CONFIG_OK_XPACK, "people"));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchIndexHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponseList("/es/mock-responses/es-get-indexes-1.json"))
        .when(elasticsearchIndexHealthCheck)
        .performRequestList(Mockito.any(), eq("GET"), eq(ES_INDEXES_ENDPOINT));
    doReturn(loadMockResponseList("/es/mock-responses/es-get-aliases-1.json"))
        .when(elasticsearchIndexHealthCheck)
        .performRequestList(Mockito.any(), eq("GET"), eq(ES_ALIASES_ENDPOINT));
    Result result = elasticsearchIndexHealthCheck.check();

    assertNotNull(result);
    assertTrue(result.isHealthy());
    assertEquals(String.format(HEALTHY_ES_INDEX_MSG, "people"), result.getMessage());
  }

  @Test
  public void testElasticsearchIndexNotAvailable() throws Exception {
    ElasticsearchIndexHealthCheck elasticsearchIndexHealthCheck =
        spy(new ElasticsearchIndexHealthCheck(CONFIG_OK_XPACK, PEOPLE_ALIAS));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchIndexHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponseList("/es/mock-responses/es-get-indexes-2.json"))
        .when(elasticsearchIndexHealthCheck)
        .performRequestList(Mockito.any(), eq("GET"), eq(ES_INDEXES_ENDPOINT));
    doReturn(loadMockResponseList("/es/mock-responses/es-get-aliases-1.json"))
        .when(elasticsearchIndexHealthCheck)
        .performRequestList(Mockito.any(), eq("GET"), eq(ES_ALIASES_ENDPOINT));

    Result result = elasticsearchIndexHealthCheck.check();

    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(String.format(UNHEALTHY_ES_INDEX_MSG, PEOPLE_ALIAS), result.getMessage());
  }

  @Test
  public void testElasticsearchAliasAvailable() throws Exception {
    ElasticsearchIndexHealthCheck elasticsearchIndexHealthCheck =
        spy(new ElasticsearchIndexHealthCheck(CONFIG_OK_XPACK, PEOPLE_ALIAS));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchIndexHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponseList("/es/mock-responses/es-get-indexes-2.json"))
        .when(elasticsearchIndexHealthCheck)
        .performRequestList(Mockito.any(), eq("GET"), eq(ES_INDEXES_ENDPOINT));
    doReturn(loadMockResponseList("/es/mock-responses/es-get-aliases-2.json"))
        .when(elasticsearchIndexHealthCheck)
        .performRequestList(Mockito.any(), eq("GET"), eq(ES_ALIASES_ENDPOINT));

    Result result = elasticsearchIndexHealthCheck.check();

    assertNotNull(result);
    assertTrue(result.isHealthy());
    assertEquals(String.format(HEALTHY_ES_INDEX_MSG, PEOPLE_ALIAS), result.getMessage());
  }

  @Test
  public void testElasticsearchAliasNotAvailable() throws Exception {
    ElasticsearchIndexHealthCheck elasticsearchIndexHealthCheck =
        spy(new ElasticsearchIndexHealthCheck(CONFIG_OK_XPACK, PEOPLE_ALIAS));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchIndexHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponseList("/es/mock-responses/es-get-indexes-2.json"))
        .when(elasticsearchIndexHealthCheck)
        .performRequestList(Mockito.any(), eq("GET"), eq(ES_INDEXES_ENDPOINT));
    doReturn(loadMockResponseList("/es/mock-responses/es-get-aliases-1.json"))
        .when(elasticsearchIndexHealthCheck)
        .performRequestList(Mockito.any(), eq("GET"), eq(ES_ALIASES_ENDPOINT));

    Result result = elasticsearchIndexHealthCheck.check();

    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(String.format(UNHEALTHY_ES_INDEX_MSG, PEOPLE_ALIAS), result.getMessage());
  }

  @Test
  public void testElasticsearchRoleAvailable() throws Exception {
    ElasticsearchRolesHealthCheck elasticsearchRolesHealthCheck =
        spy(new ElasticsearchRolesHealthCheck(CONFIG_OK_XPACK, WORKER_ROLE));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchRolesHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponse("/es/mock-responses/es-get-roles-1.json"))
        .when(elasticsearchRolesHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq(ES_ROLES_ENDPOINT));
    Result result = elasticsearchRolesHealthCheck.check();

    assertNotNull(result);
    assertTrue(result.isHealthy());
    assertEquals(String.format(HEALTHY_ES_ROLES_MSG, WORKER_ROLE), result.getMessage());
  }

  @Test
  public void testElasticsearchRoleNotAvailable() throws Exception {
    ElasticsearchRolesHealthCheck elasticsearchRolesHealthCheck =
        spy(new ElasticsearchRolesHealthCheck(CONFIG_OK_XPACK, WORKER_ROLE));
    doReturn(loadMockResponse("/es/mock-responses/es-get.json")).when(elasticsearchRolesHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq("/"));
    doReturn(loadMockResponse("/es/mock-responses/es-get-roles-2.json"))
        .when(elasticsearchRolesHealthCheck)
        .performRequest(Mockito.any(), eq("GET"), eq(ES_ROLES_ENDPOINT));
    Result result = elasticsearchRolesHealthCheck.check();

    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(String.format(UNHEALTHY_ES_ROLES_MSG, WORKER_ROLE), result.getMessage());
  }

  private void assertIncorrectElasticsearchConfiguration(DoraConfiguration config)
      throws Exception {
    Result result = new BasicDoraHealthCheck(config).check();
    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(UNHEALTHY_ES_CONFIG_MSG, result.getMessage());
  }

  private void assertCorrectElasticsearchConfiguration(DoraConfiguration config)
      throws Exception {
    Result result = new BasicDoraHealthCheck(config).check();
    assertNotNull(result);
    assertTrue(result.isHealthy());
    ElasticsearchConfiguration esConfig = config.getElasticsearchConfiguration();
    assertEquals(String.format(HEALTHY_ES_CONFIG_MSG, config.getMode(), esConfig.getNodes(),
        esConfig.getXpack().isEnabled() ? "enabled" : "disabled"), result.getMessage());
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
}

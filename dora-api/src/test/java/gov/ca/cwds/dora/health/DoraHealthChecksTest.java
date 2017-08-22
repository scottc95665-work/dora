package gov.ca.cwds.dora.health;

import static gov.ca.cwds.dora.health.BasicDoraHealthCheck.HEALTHY_ES_CONFIG_MSG;
import static gov.ca.cwds.dora.health.BasicDoraHealthCheck.UNHEALTHY_ES_CONFIG_MSG;
import static gov.ca.cwds.dora.health.ElasticsearchHealthCheck.UNHEALTHY_ELASTICSEARCH_MSG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.codahale.metrics.health.HealthCheck.Result;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration.XpackConfiguration;
import org.junit.Test;

public class DoraHealthChecksTest {

  @Test
  public void testElasticsearchConfigurations() throws Exception {
    assertIncorrectElasticsearchConfiguration(new ElasticsearchConfiguration("localhost", null));
    assertIncorrectElasticsearchConfiguration(new ElasticsearchConfiguration(null, "9200"));

    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration("localhost", "9200");
    XpackConfiguration xpackConfig = new XpackConfiguration();
    xpackConfig.setEnabled(true);
    esConfig.setXpack(xpackConfig);
    assertIncorrectElasticsearchConfiguration(esConfig);

    xpackConfig.setUser("user");
    xpackConfig.setPassword("password");
    Result result = new BasicDoraHealthCheck(esConfig).check();
    assertNotNull(result);
    assertTrue(result.isHealthy());
    assertEquals(String.format(HEALTHY_ES_CONFIG_MSG, "localhost", "9200", "enabled"),
        result.getMessage());
  }

  @Test
  public void testElasticsearchUnavailable() throws Exception {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration("localhost", "9999");
    XpackConfiguration xpackConfig = new XpackConfiguration();
    xpackConfig.setEnabled(false);
    esConfig.setXpack(xpackConfig);

    Result result = new ElasticsearchHealthCheck(esConfig).check();
    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(UNHEALTHY_ELASTICSEARCH_MSG + "Connection refused: no further information",
        result.getMessage());
  }

  private void assertIncorrectElasticsearchConfiguration(ElasticsearchConfiguration esConfig)
      throws Exception {
    Result result = new BasicDoraHealthCheck(esConfig).check();
    assertNotNull(result);
    assertFalse(result.isHealthy());
    assertEquals(UNHEALTHY_ES_CONFIG_MSG, result.getMessage());
  }
}

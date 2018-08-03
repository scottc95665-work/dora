package gov.ca.cwds.dora;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import gov.ca.cwds.rest.ElasticsearchConfiguration;
import org.apache.http.HttpHost;
import org.junit.Test;

/**
 * CWDS API Team
 */
public class DoraUtilsTest {

  @Test
  public void testParseNodes() {
    HttpHost[] httpHosts = DoraUtils.parseNodes("a:1,b:,:3");
    assertEquals(2, httpHosts.length);

    assertEquals("a", httpHosts[0].getHostName());
    assertEquals(1, httpHosts[0].getPort());

    assertEquals("b", httpHosts[1].getHostName());
    assertEquals(-1, httpHosts[1].getPort());
  }

  @Test
  public void testCreateElasticsearchClient() {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    esConfig.setNodes("localhost:9200");
    esConfig.setUser("user");
    esConfig.setPassword("password");
    assertNotNull(DoraUtils.createElasticsearchClient(esConfig));
  }
}

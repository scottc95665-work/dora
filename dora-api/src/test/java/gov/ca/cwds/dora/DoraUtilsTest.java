package gov.ca.cwds.dora;

import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.security.realm.PerrySubject;
import org.apache.http.HttpHost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * CWDS API Team
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PerrySubject.class)
@PowerMockIgnore({ "javax.net.ssl.*" })
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

  @Test
  public void createAuthorizedElasticsearchClient() {
    mockStatic(PerrySubject.class);
    when(PerrySubject.getToken()).thenReturn("");

    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    esConfig.setNodes("localhost:9200");
    esConfig.setUser("user");
    esConfig.setPassword("password");
    assertNotNull(DoraUtils.createXpackElasticsearchClient(esConfig));
  }
}
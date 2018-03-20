package gov.ca.cwds.dora;

import org.apache.http.HttpHost;
import org.junit.Assert;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 * CWDS API Team
 */
public class DoraUtilsTest {

  @Test
  public void testParseNodes() throws Exception {
    HttpHost[] httpHosts = DoraUtils.parseNodes("a:1,b:,:3");
    assertEquals(3, httpHosts.length);

    assertEquals("a", httpHosts[0].getHostName());
    assertEquals(1, httpHosts[0].getPort());

    assertEquals("b", httpHosts[1].getHostName());
    assertEquals(-1, httpHosts[1].getPort());

    assertEquals("", httpHosts[2].getHostName());
    assertEquals(3, httpHosts[2].getPort());
  }
}
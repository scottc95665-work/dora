package gov.ca.cwds.inject;

import static org.junit.Assert.assertNotNull;

import gov.ca.cwds.rest.ElasticsearchConfiguration;
import org.junit.Test;

public class EsRestClientProviderTest {

  @Test
  public void testGetEsRestClient() {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    esConfig.setNodes("localhost:9200");
    esConfig.setUser("user");
    esConfig.setPassword("password");

    EsRestClientProvider esRestClientProvider = new EsRestClientProvider(esConfig);
    assertNotNull(esRestClientProvider.get());
  }
}

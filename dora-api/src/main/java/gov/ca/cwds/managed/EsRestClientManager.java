package gov.ca.cwds.managed;

import static gov.ca.cwds.dora.DoraUtils.createElasticsearchClient;

import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import io.dropwizard.lifecycle.Managed;
import java.io.IOException;
import java.util.Optional;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsRestClientManager implements Managed {

  private static final Logger LOGGER = LoggerFactory.getLogger(EsRestClientManager.class);

  private static RestClient esRestClient;

  public EsRestClientManager(ElasticsearchConfiguration esConfig) {
    esRestClient = Optional.ofNullable(esRestClient).orElse(createElasticsearchClient(esConfig));
    LOGGER.info("\n********* Elasticsearch Rest Client is created *********");
  }

  @Override
  public void start() {
    // no-op
  }

  @Override
  public void stop() {
    try {
      LOGGER.debug("\n********* EsRestClientManager stop is invoked *********");
      if (esRestClient != null) {
        esRestClient.close();
        esRestClient = null;
        LOGGER.info("\n********* Elasticsearch client is closed *********");
      }
    } catch (IOException e) {
      LOGGER.error("error closing Elasticsearch client", e);
      throw new DoraException("error closing Elasticsearch client", e);
    }
  }

  public static RestClient getEsRestClient() {
    return esRestClient;
  }
}

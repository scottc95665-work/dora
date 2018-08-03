package gov.ca.cwds.managed;

import static gov.ca.cwds.dora.DoraUtils.createElasticsearchClient;

import gov.ca.cwds.rest.ElasticSearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import io.dropwizard.lifecycle.Managed;
import java.io.IOException;
import java.util.Optional;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("findbugs:ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD") // is instantiated only once
public class EsRestClientManager implements Managed {

  private static final Logger LOGGER = LoggerFactory.getLogger(EsRestClientManager.class);

  private static RestClient esRestClient;

  public EsRestClientManager(ElasticSearchConfiguration esConfig) {
    createEsRestClient(esConfig);
  }

  private static void createEsRestClient(ElasticSearchConfiguration esConfig) {
    esRestClient = Optional.ofNullable(esRestClient).orElse(createElasticsearchClient(esConfig));
    LOGGER.info("********* Elasticsearch Rest Client is created *********");
  }

  @Override
  public void start() {
    // no-op
  }

  /**
   * This code is executed only once at the application start if properly managed.
   * E.g. environment.lifecycle().manage(...)
   * The EsRestClientManager should <b>not</b> be managed by guice.
   */
  @Override
  public void stop() {
    try {
      LOGGER.debug("********* EsRestClientManager stop is invoked *********");
      if (esRestClient != null) {
        esRestClient.close();
        LOGGER.info("********* Elasticsearch client is closed *********");
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

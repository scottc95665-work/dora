package gov.ca.cwds.rest;

import com.google.inject.Inject;
import gov.ca.cwds.rest.api.DoraException;
import io.dropwizard.lifecycle.Managed;
import java.io.IOException;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsRestClientManager implements Managed {

  private static final Logger LOGGER = LoggerFactory.getLogger(EsRestClientManager.class);

  @Inject
  private RestClient esRestClient;

  public EsRestClientManager() {
    // default constructor
  }
  
  @Override
  public void start() {
    // no-op
  }

  @Override
  public void stop() {
    try {
      esRestClient.close();
      LOGGER.info("Elasticsearch client is closed");
    } catch (IOException e) {
      throw new DoraException("error closing Elasticsearch client", e);
    }
  }

  public RestClient getEsRestClient() {
    return esRestClient;
  }
}

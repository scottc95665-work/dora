package gov.ca.cwds.inject;

import com.google.inject.AbstractModule;
import org.elasticsearch.client.RestClient;

public class EsRestClientModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(RestClient.class).toProvider(EsRestClientProvider.class);
  }
}

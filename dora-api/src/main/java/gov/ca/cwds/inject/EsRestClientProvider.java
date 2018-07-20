package gov.ca.cwds.inject;

import static gov.ca.cwds.dora.DoraUtils.createElasticsearchClient;

import com.google.inject.Inject;
import com.google.inject.Provider;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import org.elasticsearch.client.RestClient;

public class EsRestClientProvider implements Provider<RestClient> {

  private ElasticsearchConfiguration esConfig;

  @Inject
  public EsRestClientProvider(ElasticsearchConfiguration esConfig) {
    this.esConfig = esConfig;
  }

  @Override
  public RestClient get() {
    return createElasticsearchClient(esConfig);
  }
}

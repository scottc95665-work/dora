package gov.ca.cwds.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

/**
 * @author CWDS TPT-2 Team
 */
public class DoraConfiguration extends MinimalApiConfiguration {


  @NotNull
  private ElasticSearchConfiguration elasticsearchConfiguration;

  @JsonProperty(value = "elasticsearch")
  public ElasticSearchConfiguration getElasticsearchConfiguration() {
    return elasticsearchConfiguration;
  }

  @JsonProperty
  public void setElasticsearchConfiguration(ElasticSearchConfiguration elasticsearchConfiguration) {
    this.elasticsearchConfiguration = elasticsearchConfiguration;
  }

}

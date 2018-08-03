package gov.ca.cwds.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

/**
 * @author CWDS TPT-2 Team
 */
public class DoraConfiguration extends MinimalApiConfiguration {


  @NotNull
  private ElasticsearchConfiguration elasticsearchConfiguration;

  @JsonProperty(value = "elasticsearch")
  public ElasticsearchConfiguration getElasticsearchConfiguration() {
    return elasticsearchConfiguration;
  }

  @JsonProperty
  public void setElasticsearchConfiguration(ElasticsearchConfiguration elasticsearchConfiguration) {
    this.elasticsearchConfiguration = elasticsearchConfiguration;
  }

}

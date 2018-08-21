package gov.ca.cwds.rest;

import static gov.ca.cwds.rest.DoraConstants.PROD_MODE;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

/**
 * @author CWDS TPT-2 Team
 */
public class DoraConfiguration extends MinimalApiConfiguration {


  @JsonProperty
  private String mode;

  @NotNull
  private ElasticsearchConfiguration elasticsearchConfiguration;

  public String getMode() {
    return mode == null ? PROD_MODE : mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  @JsonProperty(value = "elasticsearch")
  public ElasticsearchConfiguration getElasticsearchConfiguration() {
    return elasticsearchConfiguration;
  }

  @JsonProperty
  public void setElasticsearchConfiguration(ElasticsearchConfiguration elasticsearchConfiguration) {
    this.elasticsearchConfiguration = elasticsearchConfiguration;
  }

}

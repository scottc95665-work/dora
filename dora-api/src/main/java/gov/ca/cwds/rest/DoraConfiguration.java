package gov.ca.cwds.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.secnod.dropwizard.shiro.ShiroConfiguration;

/**
 * @author CWDS TPT-2 Team
 */
public class DoraConfiguration extends Configuration {

  /**
   * The application name
   */
  @NotEmpty
  private String applicationName;

  /**
   * The version
   */
  @NotEmpty
  private String version;

  private SwaggerConfiguration swaggerConfiguration;

  @NotNull
  private ElasticsearchConfiguration elasticsearchConfiguration;

  @Nullable
  private ShiroConfiguration shiroConfiguration;

  @JsonProperty
  public String getApplicationName() {
    return applicationName;
  }

  @JsonProperty
  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  /**
   * @return the version
   */
  @JsonProperty
  public String getVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  @JsonProperty
  public void setVersion(String version) {
    this.version = version;
  }

  @JsonProperty(value = "swagger")
  public SwaggerConfiguration getSwaggerConfiguration() {
    return swaggerConfiguration;
  }

  @JsonProperty
  public void setSwaggerConfiguration(SwaggerConfiguration swaggerConfiguration) {
    this.swaggerConfiguration = swaggerConfiguration;
  }


  @JsonProperty(value = "elasticsearch")
  public ElasticsearchConfiguration getElasticsearchConfiguration() {
    return elasticsearchConfiguration;
  }

  @JsonProperty
  public void setElasticsearchConfiguration(ElasticsearchConfiguration elasticsearchConfiguration) {
    this.elasticsearchConfiguration = elasticsearchConfiguration;
  }

  @JsonProperty(value = "shiro")
  public ShiroConfiguration getShiroConfiguration() {
    return shiroConfiguration;
  }

  @JsonProperty
  public void setShiroConfiguration(ShiroConfiguration shiroConfiguration) {
    this.shiroConfiguration = shiroConfiguration;
  }
}

package gov.ca.cwds.rest;

import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author CWDS Elasticsearch Team
 */
public class ElasticsearchConfiguration {

  @NotEmpty
  @JsonProperty("nodes")
  private String nodes;

  @JsonProperty
  private String user;

  @JsonProperty
  private String password;

  @NotEmpty
  @JsonProperty
  private Map<String, String> responseFieldFilters;

  public ElasticsearchConfiguration() {
    // no op
  }

  public String getNodes() {
    return nodes;
  }

  public void setNodes(String nodes) {
    this.nodes = nodes;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Map<String, String> getResponseFieldFilters() {
    return responseFieldFilters;
  }

  public static class XpackConfiguration {
    @JsonProperty
    private boolean enabled;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }

  private XpackConfiguration xpack;

  public XpackConfiguration getXpack() {
    return xpack;
  }

  @JsonProperty
  public void setXpack(XpackConfiguration xpack) {
    this.xpack = xpack;
  }

}

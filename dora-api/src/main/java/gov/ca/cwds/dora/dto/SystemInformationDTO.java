package gov.ca.cwds.dora.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.SortedMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author CWDS TPT-2
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SystemInformationDTO {
  private String applicationName;
  private String version;
  private SortedMap<String, HealthCheckResultDTO> healthCheckResults;

  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public SortedMap<String, HealthCheckResultDTO> getHealthCheckResults() {
    return healthCheckResults;
  }

  public void setHealthCheckResults(
      SortedMap<String, HealthCheckResultDTO> healthCheckResults) {
    this.healthCheckResults = healthCheckResults;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}

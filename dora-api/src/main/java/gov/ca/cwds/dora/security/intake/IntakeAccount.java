package gov.ca.cwds.dora.security.intake;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import gov.ca.cwds.auth.PerryUserIdentity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IntakeAccount extends PerryUserIdentity {

  @JsonProperty("county_code")
  private String countyCode;

  @JsonProperty("county_name")
  private String countyName;

  @JsonProperty("government_entity_type")
  private String governmentEntityType;

  @JsonProperty
  private Set<String> privileges;

  @Override
  public String getCountyCode() {
    return countyCode;
  }

  @Override
  public void setCountyCode(String countyCode) {
    this.countyCode = countyCode;
  }

  @Override
  public String getCountyName() {
    return countyName;
  }

  @Override
  public void setCountyName(String countyName) {
    this.countyName = countyName;
  }

  public String getGovernmentEntityType() {
    return governmentEntityType;
  }

  public void setGovernmentEntityType(String governmentEntityType) {
    this.governmentEntityType = governmentEntityType;
  }

  @Override
  public Set<String> getPrivileges() {
    return privileges;
  }

  @Override
  public void setPrivileges(Set<String> privileges) {
    this.privileges = privileges;
  }

}

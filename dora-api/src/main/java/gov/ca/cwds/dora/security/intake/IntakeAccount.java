package gov.ca.cwds.dora.security.intake;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.security.shiro.realms.PerryAccount;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IntakeAccount extends PerryAccount {

  @JsonProperty
  private String staffId;

  @JsonProperty("county_code")
  private String countyCode;

  @JsonProperty("county_name")
  private String countyName;

  @JsonProperty
  private Set<String> privileges;

  public String getStaffId() {
    return staffId;
  }

  public void setStaffId(String staffId) {
    this.staffId = staffId;
  }

  public String getCountyCode() {
    return countyCode;
  }

  public void setCountyCode(String countyCode) {
    this.countyCode = countyCode;
  }

  public String getCountyName() {
    return countyName;
  }

  public void setCountyName(String countyName) {
    this.countyName = countyName;
  }

  public Set<String> getPrivileges() {
    return privileges;
  }

  public void setPrivileges(Set<String> privileges) {
    this.privileges = privileges;
  }
}

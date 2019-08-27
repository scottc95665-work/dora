package gov.ca.cwds.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.ca.cwds.security.realm.PerryAccount;

/**
 * @author CWDS CALS API Team
 */
public class PerryUserIdentity extends PerryAccount {

  @JsonProperty
  private String staffId;

  @Override
  public String getStaffId() {
    return staffId;
  }

  @Override
  public void setStaffId(String staffId) {
    this.staffId = staffId;
  }

}

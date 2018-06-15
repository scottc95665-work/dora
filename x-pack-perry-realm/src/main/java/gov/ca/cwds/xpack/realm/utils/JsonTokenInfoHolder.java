package gov.ca.cwds.xpack.realm.utils;

import java.util.List;

/**
 * @author CWDS TPT-2
 */
public final class JsonTokenInfoHolder {

  private List<String> privileges;
  private List<String> roles;
  private String countyCode;
  private boolean countyIsStateOfCalifornia;

  public List<String> getPrivileges() {
    return privileges;
  }

  public void setPrivileges(List<String> privileges) {
    this.privileges = privileges;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public String getCountyCode() {
    return countyCode;
  }

  public void setCountyCode(String countyCode) {
    this.countyCode = countyCode;
  }

  public boolean isCountyIsStateOfCalifornia() {
    return countyIsStateOfCalifornia;
  }

  public void setCountyIsStateOfCalifornia(
      boolean countyIsStateOfCalifornia) {
    this.countyIsStateOfCalifornia = countyIsStateOfCalifornia;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    JsonTokenInfoHolder holder = (JsonTokenInfoHolder) o;

    return countyIsStateOfCalifornia == holder.countyIsStateOfCalifornia
        && (privileges != null ? privileges.equals(holder.privileges) : holder.privileges == null)
        && (roles != null ? roles.equals(holder.roles) : holder.roles == null)
        && (countyCode != null ? countyCode.equals(holder.countyCode) : holder.countyCode == null);
  }

  @Override
  public int hashCode() {
    int result = privileges != null ? privileges.hashCode() : 0;
    result = 31 * result + (roles != null ? roles.hashCode() : 0);
    result = 31 * result + (countyCode != null ? countyCode.hashCode() : 0);
    result = 31 * result + (countyIsStateOfCalifornia ? 1 : 0);
    return result;
  }
}

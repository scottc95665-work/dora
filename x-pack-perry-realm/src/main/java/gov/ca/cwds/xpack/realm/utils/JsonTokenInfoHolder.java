package gov.ca.cwds.xpack.realm.utils;

import java.util.List;
import java.util.Set;

/**
 * @author CWDS TPT-2
 */
public final class JsonTokenInfoHolder {

  private List<String> privileges;
  private Set<String> roles;
  private String countyCode;
  private String countyName;
  private Set<String> adminOfficeIds;
  private boolean countyIsStateOfCalifornia;

  public List<String> getPrivileges() {
    return privileges;
  }

  public void setPrivileges(List<String> privileges) {
    this.privileges = privileges;
  }

  public Set<String> getRoles() {
    return roles;
  }

  public void setRoles(Set<String> roles) {
    this.roles = roles;
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

  public boolean isCountyIsStateOfCalifornia() {
    return countyIsStateOfCalifornia;
  }

  public void setCountyIsStateOfCalifornia(
      boolean countyIsStateOfCalifornia) {
    this.countyIsStateOfCalifornia = countyIsStateOfCalifornia;
  }

  public Set<String> getAdminOfficeIds() {
    return adminOfficeIds;
  }

  public void setAdminOfficeIds(Set<String> adminOfficeIds) {
    this.adminOfficeIds = adminOfficeIds;
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
        && (countyCode != null ? countyCode.equals(holder.countyCode) : holder.countyCode == null)
        && (countyName != null ? countyName.equals(holder.countyName) : holder.countyName == null)
        && (adminOfficeIds != null ? adminOfficeIds.equals(holder.adminOfficeIds) : holder.adminOfficeIds == null);
  }

  @Override
  public int hashCode() {
    int result = privileges != null ? privileges.hashCode() : 0;
    result = 31 * result + (roles != null ? roles.hashCode() : 0);
    result = 31 * result + (countyCode != null ? countyCode.hashCode() : 0);
    result = 31 * result + (countyName != null ? countyName.hashCode() : 0);
    result = 31 * result + (countyIsStateOfCalifornia ? 1 : 0);
    result = 31 * result + (adminOfficeIds != null ? adminOfficeIds.hashCode() : 0);
    return result;
  }
}

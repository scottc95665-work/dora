package gov.ca.cwds.xpack.realm.utils;

import java.util.List;

/**
 * @author CWDS TPT-2
 */
public final class JsonTokenInfoHolder {

  private List<String> privileges;
  private String countyCode;
  private boolean governmentEntityTypeIsStateOfCalifornia;

  public List<String> getPrivileges() {
    return privileges;
  }

  public void setPrivileges(List<String> privileges) {
    this.privileges = privileges;
  }

  public String getCountyCode() {
    return countyCode;
  }

  public void setCountyCode(String countyCode) {
    this.countyCode = countyCode;
  }

  public boolean isGovernmentEntityTypeIsStateOfCalifornia() {
    return governmentEntityTypeIsStateOfCalifornia;
  }

  public void setGovernmentEntityTypeIsStateOfCalifornia(
      boolean governmentEntityTypeIsStateOfCalifornia) {
    this.governmentEntityTypeIsStateOfCalifornia = governmentEntityTypeIsStateOfCalifornia;
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

    return governmentEntityTypeIsStateOfCalifornia == holder.governmentEntityTypeIsStateOfCalifornia
        && (privileges != null ? privileges.equals(holder.privileges) : holder.privileges == null)
        && (countyCode != null ? countyCode.equals(holder.countyCode) : holder.countyCode == null);
  }

  @Override
  public int hashCode() {
    int result = privileges != null ? privileges.hashCode() : 0;
    result = 31 * result + (countyCode != null ? countyCode.hashCode() : 0);
    result = 31 * result + (governmentEntityTypeIsStateOfCalifornia ? 1 : 0);
    return result;
  }
}

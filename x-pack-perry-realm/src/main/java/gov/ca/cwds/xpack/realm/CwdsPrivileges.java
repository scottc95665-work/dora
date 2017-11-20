package gov.ca.cwds.xpack.realm;

import static gov.ca.cwds.xpack.realm.utils.Constants.CWS_CASE_MANAGEMENT_SYSTEM;
import static gov.ca.cwds.xpack.realm.utils.Constants.SEALED;
import static gov.ca.cwds.xpack.realm.utils.Constants.SENSITIVE_PERSONS;
import static gov.ca.cwds.xpack.realm.utils.PerryRealmUtils.countyCodeToCountyId;
import static gov.ca.cwds.xpack.realm.utils.PerryRealmUtils.parsePerryTokenFromJSON;

import gov.ca.cwds.xpack.realm.utils.JsonTokenInfoHolder;
import java.io.IOException;

/**
 * @author CWDS TPT-2
 */
public final class CwdsPrivileges {

  private boolean countySensitive = false;
  private boolean socialWorkerOnly = false;
  private boolean countySealed = false;
  private boolean stateSensitive = false;
  private boolean stateSealed = false;
  private String countyId = "";

  private CwdsPrivileges() {
    // no op
  }

  /**
   * Use this method to parse Json Token like the following:
   * <pre>
   * {
   * "user": "RACFID",
   * "staffId": "34",
   * "roles": ["Supervisor"],
   * "county_code": "19",
   * "county_name": "Los Angeles",
   * "privileges": [
   * "Statewide Read",
   * "Sensitive Persons"
   * ]
   * }
   * </pre>
   * and return an instance of CwdsPrivileges with properties that corresponds to the Json Token.
   *
   * @param json the Json Token
   * @return instance of CwdsPrivileges
   * @throws IOException if can't parse the token
   */
  @SuppressWarnings("squid:S3776") // this method pretty straightforward
  public static CwdsPrivileges fromJson(String json) throws IOException {
    CwdsPrivileges cwdsPrivileges = new CwdsPrivileges();

    JsonTokenInfoHolder holder = parsePerryTokenFromJSON(json);

    // JWT token will contain County Code, but Person documents in ES index and X-Pack roles use County ID
    cwdsPrivileges.countyId = countyCodeToCountyId(holder.getCountyCode());

    cwdsPrivileges.socialWorkerOnly = holder.getPrivileges().contains(CWS_CASE_MANAGEMENT_SYSTEM);
    cwdsPrivileges.countySensitive = holder.getPrivileges().contains(SENSITIVE_PERSONS) && (!holder
        .isGovernmentEntityTypeIsStateOfCalifornia());
    cwdsPrivileges.countySealed = holder.getPrivileges().contains(SEALED) && (!holder
        .isGovernmentEntityTypeIsStateOfCalifornia());
    cwdsPrivileges.stateSensitive = holder.getPrivileges().contains(SENSITIVE_PERSONS) && holder
        .isGovernmentEntityTypeIsStateOfCalifornia();
    cwdsPrivileges.stateSealed = holder.getPrivileges().contains(SEALED) && holder
        .isGovernmentEntityTypeIsStateOfCalifornia();

    return cwdsPrivileges;
  }

  public boolean isCountySensitive() {
    return countySensitive;
  }

  public boolean isCountySealed() {
    return countySealed;
  }

  public boolean isStateSensitive() {
    return stateSensitive;
  }

  public boolean isStateSealed() {
    return stateSealed;
  }

  public String getCountyId() {
    return countyId;
  }

  public boolean isSocialWorkerOnly() {
    return socialWorkerOnly;
  }

  @Override
  public String toString() {
    return "CwdsPrivileges{" +
        "countySensitive=" + countySensitive +
        ", socialWorkerOnly=" + socialWorkerOnly +
        ", countySealed=" + countySealed +
        ", stateSensitive=" + stateSensitive +
        ", stateSealed=" + stateSealed +
        ", countyId='" + countyId + '\'' +
        '}';
  }
}

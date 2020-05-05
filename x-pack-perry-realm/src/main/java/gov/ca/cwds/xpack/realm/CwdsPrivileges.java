package gov.ca.cwds.xpack.realm;

import static gov.ca.cwds.xpack.realm.utils.Constants.ADOPTIONS;
import static gov.ca.cwds.xpack.realm.utils.Constants.CWS_CASE_MANAGEMENT_SYSTEM;
import static gov.ca.cwds.xpack.realm.utils.Constants.RESOURCE_MANAGEMENT;
import static gov.ca.cwds.xpack.realm.utils.Constants.SEALED;
import static gov.ca.cwds.xpack.realm.utils.Constants.SENSITIVE_PERSONS;
import static gov.ca.cwds.xpack.realm.utils.PerryRealmUtils.countyCodeToCountyId;
import static gov.ca.cwds.xpack.realm.utils.PerryRealmUtils.parsePerryTokenFromJSON;

import gov.ca.cwds.xpack.realm.utils.JsonTokenInfoHolder;

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
  private String countyName = "";
  private boolean facilitiesRead = false;
  private boolean facilitiesReadAdoptions = false;

  private CwdsPrivileges() {
    // no op
  }

  /**
   * Use this method to parse JSON Token like the following:
   * 
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
   * 
   * and return an instance of CwdsPrivileges with properties that corresponds to the Json Token.
   *
   * @param json the JSON Token
   * @return instance of CwdsPrivileges
   * @throws IllegalArgumentException if can't parse the token
   */
  @SuppressWarnings("squid:S3776") // this method pretty straightforward
  public static CwdsPrivileges fromJson(String json) {
    JsonTokenInfoHolder holder = parsePerryTokenFromJSON(json);
    return buildPrivileges(holder);
  }

  public static CwdsPrivileges buildPrivileges(JsonTokenInfoHolder holder) {
    CwdsPrivileges cwdsPrivileges = new CwdsPrivileges();
    // JWT token will contain County Code, but Person documents in ES index and X-Pack roles use
    // County ID
    cwdsPrivileges.countyId = countyCodeToCountyId(holder.getCountyCode());
    cwdsPrivileges.countyName = holder.getCountyName();
    cwdsPrivileges.socialWorkerOnly = holder.getPrivileges().contains(CWS_CASE_MANAGEMENT_SYSTEM);
    cwdsPrivileges.countySensitive = isCountySensitive(holder);
    cwdsPrivileges.countySealed = isCountySealed(holder);
    cwdsPrivileges.stateSensitive = isStateSensitive(holder);
    cwdsPrivileges.stateSealed = isStateSealed(holder);
    cwdsPrivileges.facilitiesRead = isFacilitiesRead(holder);
    cwdsPrivileges.facilitiesReadAdoptions =
        isFacilitiesReadAdoptions(holder, cwdsPrivileges.facilitiesRead);
    return cwdsPrivileges;
  }

  private static boolean isFacilitiesReadAdoptions(JsonTokenInfoHolder holder,
      boolean isFacilitiesRead) {
    return isFacilitiesRead && holder.getPrivileges().contains(ADOPTIONS);
  }

  private static boolean isFacilitiesRead(JsonTokenInfoHolder holder) {
    return holder.getPrivileges().contains(RESOURCE_MANAGEMENT)
        || holder.getPrivileges().contains(CWS_CASE_MANAGEMENT_SYSTEM);
  }

  private static boolean isStateSealed(JsonTokenInfoHolder holder) {
    return holder.getPrivileges().contains(SEALED) && holder.isCountyIsStateOfCalifornia();
  }

  private static boolean isStateSensitive(JsonTokenInfoHolder holder) {
    return holder.getPrivileges().contains(SENSITIVE_PERSONS)
        && holder.isCountyIsStateOfCalifornia();
  }

  private static boolean isCountySealed(JsonTokenInfoHolder holder) {
    return holder.getPrivileges().contains(SEALED) && (!holder.isCountyIsStateOfCalifornia());
  }

  private static boolean isCountySensitive(JsonTokenInfoHolder holder) {
    return holder.getPrivileges().contains(SENSITIVE_PERSONS)
        && (!holder.isCountyIsStateOfCalifornia());
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

  public String getCountyName() {
    return countyName;
  }

  public boolean isSocialWorkerOnly() {
    return socialWorkerOnly;
  }

  public boolean isFacilitiesRead() {
    return facilitiesRead;
  }

  public boolean isFacilitiesReadAdoptions() {
    return facilitiesReadAdoptions;
  }

  @Override
  public String toString() {
    return "CwdsPrivileges{" + "countySensitive=" + countySensitive + ", socialWorkerOnly="
        + socialWorkerOnly + ", countySealed=" + countySealed + ", stateSensitive=" + stateSensitive
        + ", stateSealed=" + stateSealed + ", facilitiesRead=" + facilitiesRead
        + ", facilitiesReadAdoptions=" + facilitiesReadAdoptions + ", countyId='" + countyId + '\''
        + ", countyName='" + countyName + '\'' + '}';
  }

}

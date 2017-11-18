package gov.ca.cwds.xpack.realm;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;

/**
 * @author CWDS TPT-2
 */
public final class CwdsPrivileges {

  private static final int COUNTY_CODE_TO_ID_DELTA = 1067;
  private boolean countySensitive = false;
  private boolean countySealed = false;
  private boolean stateSensitive = false;
  private boolean stateSealed = false;
  private String countyId = "";

  private static boolean isGovernmentEntityTypeIsStateOfCalifornia;

  private static JsonFactory jsonFactory;

  static {
    jsonFactory = new JsonFactory();
  }

  private CwdsPrivileges() {
    // no op
  }

  /**
   * Use this method to parse Json Token like the following:
   <pre>
   {
     "user": "RACFID",
     "staffId": "34",
     "roles": ["Supervisor"],
     "county_code": "19",
     "county_name": "Los Angeles",
     "privileges": [
       "Statewide Read",
       "Sensitive Persons"
     ]
   }
   </pre>
   * and return an instance of CwdsPrivileges with properties that corresponds to the Json Token.
   *
   * @param json the Json Token
   * @return instance of CwdsPrivileges
   * @throws IOException if can't parse the token
   */
  @SuppressWarnings("squid:S3776") // this method pretty straightforward
  public static CwdsPrivileges fromJson(String json) throws IOException {
    CwdsPrivileges cwdsPrivileges = new CwdsPrivileges();

    boolean hasSealed = false;
    boolean hasSensitive = false;
    boolean hasCountywideRead = false;
    boolean hasStatewideRead = false;

    // new vars
    String governmentEntityType;
    boolean socialWorkerOnly = false;
    boolean countySensitive = false;
    boolean countySealed = false;
    boolean stateSensitive = false;
    boolean stateSealed = false;

    try (JsonParser parser = jsonFactory.createParser(json)) {

      checkThatGovernmentEntityTypeIsStateOfCalifornia(parser);

      while (parser.nextToken() != JsonToken.END_OBJECT) {
        String fieldName = parser.getCurrentName();

        if ("county_code".equals(fieldName)) {
          parser.nextToken(); // current token is "county_code", move to its value

          // JWT token will contain County Code, but Person documents in ES index and X-Pack roles use County ID
          cwdsPrivileges.countyId = countyCodeToCountyId(parser.getValueAsString());

        } else if ("privileges".equals(fieldName)) {
          parser.nextToken(); // current token is "[", move next
          // messages is array, loop until token equal to "]"
          while (parser.nextToken() != JsonToken.END_ARRAY) {
            String privilege = parser.getValueAsString().trim();

            //determine 1. Social Worker Only Privilege
            if ("CWS Case Management System".equalsIgnoreCase(privilege)) {
              socialWorkerOnly = true;
            } else if ("Sensitive Persons".equalsIgnoreCase(privilege)) {
              //determine 2. County Sensitive Privilege
              countySensitive = !isGovernmentEntityTypeIsStateOfCalifornia;

              //determine 4. State Sensitive Privilege
              stateSensitive = isGovernmentEntityTypeIsStateOfCalifornia;
            }
            //determine 3. County Sealed Privilege
            else if ("Sealed".equalsIgnoreCase(privilege)) {
              countySealed = !isGovernmentEntityTypeIsStateOfCalifornia;

              //determine 5. State Sealed Privilege
              countySealed = isGovernmentEntityTypeIsStateOfCalifornia;
            }
          }
        }
      }
    }

    cwdsPrivileges.countySensitive = hasCountywideRead && hasSensitive;
    cwdsPrivileges.countySealed = hasCountywideRead && hasSealed;
    cwdsPrivileges.stateSensitive = hasStatewideRead && hasSensitive;
    cwdsPrivileges.stateSealed = hasStatewideRead && hasSealed;

    return cwdsPrivileges;
  }

  private static void checkThatGovernmentEntityTypeIsStateOfCalifornia(
      JsonParser parser) throws IOException {
    isGovernmentEntityTypeIsStateOfCalifornia = "State of California"
        .equalsIgnoreCase(getGovernmentEntityType(parser));
  }

  private static String getGovernmentEntityType(JsonParser parser) throws IOException {
    String getGovernmentEntityType = null;

    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String fieldName = parser.getCurrentName();
      if ("government_entity_type".equals(fieldName)) {
        parser.nextToken(); // current token is "[", move next
        // messages is array, loop until token equal to "]"
        while (parser.nextToken() != JsonToken.END_ARRAY) {
          getGovernmentEntityType = parser.getValueAsString().trim();
        }
      }
    }
    return getGovernmentEntityType;
  }

  private static String countyCodeToCountyId(String countyCode) {
    try {
      return String.valueOf(Integer.parseInt(countyCode) + COUNTY_CODE_TO_ID_DELTA);
    } catch (NumberFormatException e) {
      return "";
    }
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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("CwdsPrivileges{");
    sb.append("countySensitive=").append(countySensitive);
    sb.append(", countySealed=").append(countySealed);
    sb.append(", stateSensitive=").append(stateSensitive);
    sb.append(", stateSealed=").append(stateSealed);
    sb.append(", countyId='").append(countyId).append('\'');
    sb.append('}');
    return sb.toString();
  }
}

package gov.ca.cwds.xpack.realm;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;

/**
 * @author CWDS TPT-2
 */
public final class CwdsPrivileges {
  private boolean countySensitive = false;
  private boolean countySealed = false;
  private boolean stateSensitive = false;
  private boolean stateSealed = false;
  private String countyId = "";

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

    JsonParser parser = jsonFactory.createParser(json);

    boolean hasSealed = false;
    boolean hasSensitive = false;
    boolean hasCountywideRead = false;
    boolean hasStatewideRead = false;

    while(parser.nextToken() != JsonToken.END_OBJECT){
      String fieldName = parser.getCurrentName();

      if("county_code".equals(fieldName)) {
        parser.nextToken(); // current token is "county_code", move to its value

        // JWT token will contain County Code, but Person documents in ES index and X-Pack roles use County ID
        cwdsPrivileges.countyId = countyCodeToCountyId(parser.getValueAsString());

      } else if ("privileges".equals(fieldName)){
        parser.nextToken(); // current token is "[", move next
        // messages is array, loop until token equal to "]"
        while (parser.nextToken() != JsonToken.END_ARRAY) {
          String privilege = parser.getValueAsString().trim();
          if ("Sealed".equalsIgnoreCase(privilege)) {
            hasSealed = true;
          } else if ("Sensitive Persons".equalsIgnoreCase(privilege)) {
            hasSensitive = true;
          } else if ("Countywide Read".equalsIgnoreCase(privilege)) {
            hasCountywideRead = true;
          } else if ("Countywide Read/Write".equalsIgnoreCase(privilege)) {
            hasCountywideRead = true;
          } else if ("Statewide Read".equalsIgnoreCase(privilege)) {
            hasStatewideRead = true;
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

  private static String countyCodeToCountyId(String countyCode) {
    try {
      return String.valueOf(Integer.valueOf(countyCode) + 1067);
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

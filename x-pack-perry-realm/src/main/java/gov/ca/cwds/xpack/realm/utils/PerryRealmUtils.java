package gov.ca.cwds.xpack.realm.utils;

import static gov.ca.cwds.xpack.realm.utils.Constants.COUNTY_CODE;
import static gov.ca.cwds.xpack.realm.utils.Constants.CWS_CASE_MANAGEMENT_SYSTEM;
import static gov.ca.cwds.xpack.realm.utils.Constants.GOVERNMENT_ENTITY_TYPE;
import static gov.ca.cwds.xpack.realm.utils.Constants.PRIVILEGES;
import static gov.ca.cwds.xpack.realm.utils.Constants.SEALED;
import static gov.ca.cwds.xpack.realm.utils.Constants.SENSITIVE_PERSONS;
import static gov.ca.cwds.xpack.realm.utils.Constants.STATE_OF_CALIFORNIA;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author CWDS TPT-2
 */
public final class PerryRealmUtils {

  private static JsonFactory jsonFactory;

  static {
    jsonFactory = new JsonFactory();
  }

  public static JsonTokenInfoHolder parsePerryTokenFromJSON(String json)
      throws IOException {
    JsonParser parser = jsonFactory.createParser(json);
    JsonTokenInfoHolder holder = new JsonTokenInfoHolder();
    List<String> privileges = new LinkedList<>();

    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String fieldName = parser.getCurrentName();
      if (COUNTY_CODE.equals(fieldName)) {
        parser.nextToken(); // current token is "county_code", move to its value
        holder.setCountyCode(parser.getValueAsString().trim());
      } else if (PRIVILEGES.equals(fieldName)) {
        parser.nextToken(); // current token is "[", move next
        // messages is array, loop until token equal to "]"
        while (parser.nextToken() != JsonToken.END_ARRAY) {
          String privilege = parser.getValueAsString().trim();
          if (CWS_CASE_MANAGEMENT_SYSTEM.equalsIgnoreCase(privilege)) {
            privileges.add(CWS_CASE_MANAGEMENT_SYSTEM);
          } else if (SENSITIVE_PERSONS.equalsIgnoreCase(privilege)) {
            privileges.add(SENSITIVE_PERSONS);
          } else if (SEALED.equalsIgnoreCase(privilege)) {
            privileges.add(SEALED);
          }
        }
      } else if (GOVERNMENT_ENTITY_TYPE.equals(fieldName)) {
        parser.nextToken(); // current token is "[", move next
        // messages is array, loop until token equal to "]"
        while (parser.nextToken() != JsonToken.END_ARRAY) {
          holder.setGovernmentEntityTypeIsStateOfCalifornia(
              checkThatGovernmentEntityTypeIsStateOfCalifornia(parser.getValueAsString().trim()));
        }
      }
    }

    holder.setPrivileges(privileges);
    return holder;
  }

  private static boolean checkThatGovernmentEntityTypeIsStateOfCalifornia(
      String governmentEntityType) {
    return null != governmentEntityType && governmentEntityType
        .equalsIgnoreCase(STATE_OF_CALIFORNIA);
  }
}

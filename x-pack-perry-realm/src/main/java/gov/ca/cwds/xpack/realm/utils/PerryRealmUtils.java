package gov.ca.cwds.xpack.realm.utils;

import static gov.ca.cwds.xpack.realm.utils.Constants.ADOPTIONS;
import static gov.ca.cwds.xpack.realm.utils.Constants.COUNTY_CODE;
import static gov.ca.cwds.xpack.realm.utils.Constants.COUNTY_NAME;
import static gov.ca.cwds.xpack.realm.utils.Constants.CWS_CASE_MANAGEMENT_SYSTEM;
import static gov.ca.cwds.xpack.realm.utils.Constants.PRIVILEGES;
import static gov.ca.cwds.xpack.realm.utils.Constants.RESOURCE_MANAGEMENT;
import static gov.ca.cwds.xpack.realm.utils.Constants.SEALED;
import static gov.ca.cwds.xpack.realm.utils.Constants.SENSITIVE_PERSONS;
import static gov.ca.cwds.xpack.realm.utils.Constants.STATE_OF_CALIFORNIA;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.elasticsearch.index.mapper.ObjectMapper;

/**
 * @author CWDS TPT-2
 */
public final class PerryRealmUtils {

  private static JsonFactory jsonFactory;

  private static HashMap<String, String> countyCodeToCountyIdMap = new HashMap<>();

  static {
    jsonFactory = new JsonFactory();

    countyCodeToCountyIdMap.put("01", "1068"); // Alameda
    countyCodeToCountyIdMap.put("02", "1069"); // Alpine
    countyCodeToCountyIdMap.put("03", "1070"); // Amador
    countyCodeToCountyIdMap.put("04", "1071"); // Butte
    countyCodeToCountyIdMap.put("05", "1072"); // Calaveras
    countyCodeToCountyIdMap.put("06", "1073"); // Colusa
    countyCodeToCountyIdMap.put("07", "1074"); // Contra Costa
    countyCodeToCountyIdMap.put("08", "1075"); // Del Norte
    countyCodeToCountyIdMap.put("09", "1076"); // El Dorado
    countyCodeToCountyIdMap.put("10", "1077"); // Fresno
    countyCodeToCountyIdMap.put("11", "1078"); // Glenn
    countyCodeToCountyIdMap.put("12", "1079"); // Humboldt
    countyCodeToCountyIdMap.put("13", "1080"); // Imperial
    countyCodeToCountyIdMap.put("14", "1081"); // Inyo
    countyCodeToCountyIdMap.put("15", "1082"); // Kern
    countyCodeToCountyIdMap.put("16", "1083"); // Kings
    countyCodeToCountyIdMap.put("17", "1084"); // Lake
    countyCodeToCountyIdMap.put("18", "1085"); // Lassen
    countyCodeToCountyIdMap.put("19", "1086"); // Los Angeles
    countyCodeToCountyIdMap.put("20", "1087"); // Madera
    countyCodeToCountyIdMap.put("21", "1088"); // Marin
    countyCodeToCountyIdMap.put("22", "1089"); // Mariposa
    countyCodeToCountyIdMap.put("23", "1090"); // Mendocino
    countyCodeToCountyIdMap.put("24", "1091"); // Merced
    countyCodeToCountyIdMap.put("25", "1092"); // Modoc
    countyCodeToCountyIdMap.put("26", "1093"); // Mono
    countyCodeToCountyIdMap.put("27", "1094"); // Monterey
    countyCodeToCountyIdMap.put("28", "1095"); // Napa
    countyCodeToCountyIdMap.put("29", "1096"); // Nevada
    countyCodeToCountyIdMap.put("30", "1097"); // Orange
    countyCodeToCountyIdMap.put("31", "1098"); // Placer
    countyCodeToCountyIdMap.put("32", "1099"); // Plumas
    countyCodeToCountyIdMap.put("33", "1100"); // Riverside
    countyCodeToCountyIdMap.put("34", "1101"); // Sacramento
    countyCodeToCountyIdMap.put("35", "1102"); // San Benito
    countyCodeToCountyIdMap.put("36", "1103"); // San Bernardino
    countyCodeToCountyIdMap.put("37", "1104"); // San Diego
    countyCodeToCountyIdMap.put("38", "1105"); // San Francisco
    countyCodeToCountyIdMap.put("39", "1106"); // San Joaquin
    countyCodeToCountyIdMap.put("40", "1107"); // San Luis Obispo
    countyCodeToCountyIdMap.put("41", "1108"); // San Mateo
    countyCodeToCountyIdMap.put("42", "1109"); // Santa Barbara
    countyCodeToCountyIdMap.put("43", "1110"); // Santa Clara
    countyCodeToCountyIdMap.put("44", "1111"); // Santa Cruz
    countyCodeToCountyIdMap.put("45", "1112"); // Shasta
    countyCodeToCountyIdMap.put("46", "1113"); // Sierra
    countyCodeToCountyIdMap.put("47", "1114"); // Siskiyou
    countyCodeToCountyIdMap.put("48", "1115"); // Solano
    countyCodeToCountyIdMap.put("49", "1116"); // Sonoma
    countyCodeToCountyIdMap.put("50", "1117"); // Stanislaus
    countyCodeToCountyIdMap.put("51", "1118"); // Sutter
    countyCodeToCountyIdMap.put("52", "1119"); // Tehama
    countyCodeToCountyIdMap.put("53", "1120"); // Trinity
    countyCodeToCountyIdMap.put("54", "1121"); // Tulare
    countyCodeToCountyIdMap.put("55", "1122"); // Tuolumne
    countyCodeToCountyIdMap.put("56", "1123"); // Ventura
    countyCodeToCountyIdMap.put("57", "1124"); // Yolo
    countyCodeToCountyIdMap.put("58", "1125"); // Yuba
    countyCodeToCountyIdMap.put("99", "1126"); // State of California
  }

  public static JsonTokenInfoHolder parsePerryTokenFromJSON(String json) {
    if (null == json) {
      throw new IllegalArgumentException("JSON token is null");
    }

    if (json.isEmpty()) {
      throw new IllegalArgumentException("JSON token is empty");
    }

    JsonTokenInfoHolder holder = new JsonTokenInfoHolder();
    List<String> privileges = new LinkedList<>();

    try (JsonParser parser = jsonFactory.createParser(json)) {
      while (parser.nextToken() != null) {
        String fieldName = parser.getCurrentName();
        if (COUNTY_CODE.equals(fieldName)) {
          parser.nextToken(); // current token is "county_code", move to its value
          holder.setCountyCode(parser.getValueAsString());
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
            } else if (RESOURCE_MANAGEMENT.equalsIgnoreCase(privilege)) {
              privileges.add(RESOURCE_MANAGEMENT);
            } else if (ADOPTIONS.equalsIgnoreCase(privilege)) {
              privileges.add(ADOPTIONS);
            }
          }
        } else if (COUNTY_NAME.equals(fieldName)) {
          parser.nextToken();
          holder.setCountyIsStateOfCalifornia(
              checkThatCountyIsStateOfCalifornia(parser.getValueAsString()));
        }
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("JSON token is not valid", e);
    }

    holder.setPrivileges(privileges);
    return holder;
  }

  private static boolean checkThatCountyIsStateOfCalifornia(
      String countyName) {
    return null != countyName && countyName
        .equalsIgnoreCase(STATE_OF_CALIFORNIA);
  }

  /*
   * JWT token will contain County Code, but Person documents in ES index and X-Pack roles use County ID
   */
  public static String countyCodeToCountyId(String countyCode) {
    if (countyCode == null) {
      return "xxxx";
    }
    // normalize "5" to "05"
    String cc = countyCode.length() == 1 ? "0" + countyCode : countyCode;
    cc = countyCodeToCountyIdMap.get(cc);
    return cc == null ? "xxxx" : cc;
  }
}

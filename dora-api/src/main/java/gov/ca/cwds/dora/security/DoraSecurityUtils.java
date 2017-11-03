package gov.ca.cwds.dora.security;

import gov.ca.cwds.dora.security.intake.IntakeAccount;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DoraSecurityUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoraSecurityUtils.class);

  private DoraSecurityUtils() {
    // no op
  }

  private static String getElasticsearchRunAsUser() {
    Subject subject = SecurityUtils.getSubject();
    if (subject != null) {
      List principals = subject.getPrincipals().asList();
      if (principals.size() == 2) {
        String runAsUser = getElasticsearchRunAsUser((IntakeAccount) principals.get(1));
        LOGGER.info("runAsUser: {}", runAsUser);
        return runAsUser;
      }
    }
    return null;
  }

  public static String getElasticsearchRunAsUser(IntakeAccount account) {
    String countySensitive = "F";
    String countySealed = "F";
    String stateSensitive = "F";
    String stateSealed = "F";

    if (account.getPrivileges() != null) {
      Set<String> privileges = account.getPrivileges();
      if (hasCountywideRead(privileges) && hasSensitive(privileges)) {
        countySensitive = "T";
      }
      if (hasCountywideRead(privileges) && hasSealed(privileges)) {
        countySealed = "T";
      }
      if (hasStatewideRead(privileges) && hasSensitive(privileges)) {
        stateSensitive = "T";
      }
      if (hasStatewideRead(privileges) && hasSealed(privileges)) {
        stateSealed = "T";
      }
    }

    return String.format("%s.%s.%s.%s.%s",
        countySensitive, countySealed, stateSensitive, stateSealed,
        countyCodeToCountyId(account.getCountyCode())
    );
  }

  private static void setRunAsuser(HttpURLConnection connection) {
    String runAsUser = getElasticsearchRunAsUser();
    if (runAsUser != null) {
      connection.setRequestProperty("es-security-runas-user", runAsUser);
    }
  }

  private static void setAuthorizationHeader(HttpURLConnection connection,
      ElasticsearchConfiguration esConfig) {
    String name = esConfig.getXpack().getUser();
    String password = esConfig.getXpack().getPassword();

    String authString = name + ":" + password;
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes(StandardCharsets.UTF_8));
    String authStringEnc = new String(authEncBytes, StandardCharsets.UTF_8);
    connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
  }

  public static void applySecurity(HttpURLConnection connection,
      ElasticsearchConfiguration esConfig) {
    if (esConfig.getXpack() != null) {
      ElasticsearchConfiguration.XpackConfiguration xpackConfiguration = esConfig.getXpack();
      if (xpackConfiguration.isEnabled()) {
        setAuthorizationHeader(connection, esConfig);
        setRunAsuser(connection);
      }
    }
  }

  private static HashMap<String, String> countyCodeToCountyIdMap = new HashMap<>();

  static {
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
  }

  /*
   * JWT token will contain County Code, but Person documents in ES index and X-Pack roles use County ID
   */
  private static String countyCodeToCountyId(String countyCode) {
    if (countyCode == null) {
      return "xxxx";
    }
    // normalize "5" to "05"
    String cc = countyCode.length() == 1 ? "0" + countyCode : countyCode;
    cc = countyCodeToCountyIdMap.get(cc);
    return cc == null ? "xxxx" : cc;
  }

  private static boolean hasSealed(Set<String> privileges) {
    return privileges.contains("Sealed");
  }

  private static boolean hasSensitive(Set<String> privileges) {
    return privileges.contains("Sensitive Persons");
  }

  private static boolean hasCountywideRead(Set<String> privileges) {
    return privileges.contains("Countywide Read") || privileges.contains("Countywide Read/Write");
  }

  private static boolean hasStatewideRead(Set<String> privileges) {
    return privileges.contains("Statewide Read");
  }
}

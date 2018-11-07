package gov.ca.cwds.xpack.realm;

import static gov.ca.cwds.xpack.realm.utils.PerryRealmUtils.parsePerryTokenFromJSON;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

import gov.ca.cwds.xpack.realm.utils.JsonTokenInfoHolder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * @author CWDS TPT-2
 */
public class CwdsPrivilegesTest {

  @Test
  public void testCwdsPrivileges() {

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test1.json", false, false, false, false,
        false, false, false, "1086", "Los Angeles", toSet(), toSet());

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test2.json", false, false, true, false, false,
        false, false, "1086", "Los Angeles", toSet(), toSet());

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test3.json", false, true, false, false, false,
        false, false, "1086", "Los Angeles", toSet(), toSet());

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test4.json", false, false, true, false, false,
        false, false, "1123", "Ventura", toSet(), toSet());

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test5.json", true, true, false, false, false,
        true, false, "1123", "Ventura", toSet(), toSet());

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test6.json", false, false, false, true, true,
        false, false, "1126", "State of California", toSet(), toSet());

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test7.json", false, false, false, true, true,
        true, true, "1126", "State of California", toSet(), toSet());

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test8.json", true, true, true, false, false,
        true, true, "1087", "Madera", toSet("County-admin"), toSet());

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test9.json", true, true, true, false, false,
        true, true, "1087", "Madera", toSet("Office-admin"), toSet("NpuJq9k0Wz", "OFu4W1a00E"));

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test10.json", true, true, true, false, false,
        true, true, "1087", "Madera", toSet("State-admin"), toSet());

    testCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test11.json", true, true, true, false, false,
        true, true, "1087", "Madera", toSet("CALS-admin"), toSet());
  }

  private void testCwdsPrivilegesEqualsToJson(
      String jsonFile,
      boolean isSocialWorkerOnly,
      boolean isCountySealed,
      boolean isCountySensitive,
      boolean isStateSealed,
      boolean isStateSensitive,
      boolean facilitiesRead,
      boolean facilitiesReadAdoptions,
      String countyId,
      String countyName,
      Set<String> expectedRoles,
      Set<String> expectedAdminOfficeIds) {

    JsonTokenInfoHolder holder = parsePerryTokenFromJSON(fixture(jsonFile));
    CwdsPrivileges cwdsPrivileges = CwdsPrivileges.buildPrivileges(holder);

    assertEquals(isSocialWorkerOnly, cwdsPrivileges.isSocialWorkerOnly());
    assertEquals(isCountySealed, cwdsPrivileges.isCountySealed());
    assertEquals(isCountySensitive, cwdsPrivileges.isCountySensitive());
    assertEquals(isStateSealed, cwdsPrivileges.isStateSealed());
    assertEquals(isStateSensitive, cwdsPrivileges.isStateSensitive());
    assertEquals(countyId, cwdsPrivileges.getCountyId());
    assertEquals(countyName, cwdsPrivileges.getCountyName());
    assertEquals(facilitiesRead, cwdsPrivileges.isFacilitiesRead());
    assertEquals(facilitiesReadAdoptions, cwdsPrivileges.isFacilitiesReadAdoptions());
    assertEquals(expectedRoles, holder.getRoles());
    assertEquals(expectedAdminOfficeIds, holder.getAdminOfficeIds());
  }

  private Set<String> toSet(String... expectedRoles) {
    return new HashSet<>(Arrays.asList(expectedRoles));
  }
}

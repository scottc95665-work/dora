package gov.ca.cwds.xpack.realm;

import static gov.ca.cwds.xpack.realm.utils.PerryRealmUtils.parsePerryTokenFromJSON;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.xpack.realm.utils.JsonTokenInfoHolder;
import java.io.IOException;
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

    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test1.json", false, false, false, false, false, false, false, "1086", "Los Angeles"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test2.json", false,false, true, false, false, false, false,  "1086", "Los Angeles"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test3.json", false,true, false, false, false, false, false,"1086", "Los Angeles"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test4.json", false,false, true, false, false, false, false,"1123", "Ventura"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test5.json", true,true, false, false, false, true, false,"1123", "Ventura"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test6.json", false,false, false, true, true, false, false,"1126", "State of California"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test7.json", false,false, false, true, true, true, true,"1126", "State of California"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test8.json", true,true, true, false, false, true, true,"1087", "Madera", "County-admin"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test9.json", true,true, true, false, false, true, true,"1087", "Madera", "Office-admin"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test10.json", true,true, true, false, false, true, true,"1087", "Madera", "State-admin"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test11.json", true,true, true, false, false, true, true,"1087", "Madera", "CALS-admin"));
  }

  private boolean isCwdsPrivilegesEqualsToJson(String jsonFile, boolean isSocialWorkerOnly, boolean isCountySealed,
      boolean isCountySensitive, boolean isStateSealed, boolean isStateSensitive, boolean facilitiesRead, boolean facilitiesReadAdoptions, String countyId, String countyName, String... expectedRoles) {
    JsonTokenInfoHolder holder = parsePerryTokenFromJSON(fixture(jsonFile));
    Set<String> roles =  holder.getRoles();
    CwdsPrivileges cwdsPrivileges = CwdsPrivileges.buildPrivileges(holder);
    boolean result;

    result = isSocialWorkerOnly == cwdsPrivileges.isSocialWorkerOnly();
    result &= isCountySealed == cwdsPrivileges.isCountySealed();
    result &= isCountySensitive == cwdsPrivileges.isCountySensitive();
    result &= isStateSealed == cwdsPrivileges.isStateSealed();
    result &= isStateSensitive == cwdsPrivileges.isStateSensitive();
    result &= countyId.equals(cwdsPrivileges.getCountyId());
    result &= countyName.equals(cwdsPrivileges.getCountyName());
    result &= facilitiesRead == cwdsPrivileges.isFacilitiesRead();
    result &= facilitiesReadAdoptions == cwdsPrivileges.isFacilitiesReadAdoptions();
    result &= roles.equals(new HashSet<>(Arrays.asList(expectedRoles)));

    return result;
  }
}

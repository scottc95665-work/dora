package gov.ca.cwds.xpack.realm;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Test;

/**
 * @author CWDS TPT-2
 */
public class CwdsPrivilegesTest {

  @Test
  public void testCwdsPrivileges() throws IOException {

    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test1.json", false, false, false, false, false, false, false, "1086"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test2.json", false,false, true, false, false, false, false,"1086"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test3.json", false,true, false, false, false, false, false,"1086"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test4.json", false,false, true, false, false, false, false,"1123"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test5.json", true,true, false, false, false, true, false,"1123"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test6.json", false,false, false, true, true, false, false,"1126"));
    assertTrue(isCwdsPrivilegesEqualsToJson("fixtures/jwtToken-test7.json", false,false, false, true, true, true, true,"1126"));
  }

  private boolean isCwdsPrivilegesEqualsToJson(String jsonFile, boolean isSocialWorkerOnly, boolean isCountySealed,
      boolean isCountySensitive, boolean isStateSealed, boolean isStateSensitive, boolean facilitiesRead, boolean facilitiesReadAdoptions, String countyId) {
    CwdsPrivileges cwdsPrivileges = CwdsPrivileges.fromJson(fixture(jsonFile));
    boolean result = true;

    result = isSocialWorkerOnly == cwdsPrivileges.isSocialWorkerOnly();
    result &= isCountySealed == cwdsPrivileges.isCountySealed();
    result &= isCountySensitive == cwdsPrivileges.isCountySensitive();
    result &= isStateSealed == cwdsPrivileges.isStateSealed();
    result &= isStateSensitive == cwdsPrivileges.isStateSensitive();
    result &= countyId.equals(cwdsPrivileges.getCountyId());
    result &= facilitiesRead == cwdsPrivileges.isFacilitiesRead();
    result &= facilitiesReadAdoptions == cwdsPrivileges.isFacilitiesReadAdoptions();

    return result;
  }
}

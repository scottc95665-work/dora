package gov.ca.cwds.xpack.realm;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Test;

/**
 * @author CWDS TPT-2
 */
public class CwdsPrivilegesTest {

  @Test
  public void testCwdsPrivileges() throws IOException {
    assertCwdsPrivileges("fixtures/jwtToken-test1.json", false, false, false, false, false, false,"1086");
    assertCwdsPrivileges("fixtures/jwtToken-test2.json", false,false, true, false, false, false,"1086");
    assertCwdsPrivileges("fixtures/jwtToken-test3.json", false,true, false, false, false, false,"1086");
    assertCwdsPrivileges("fixtures/jwtToken-test4.json", false,false, true, false, false, false,"1123");
    assertCwdsPrivileges("fixtures/jwtToken-test5.json", true,true, false, false, false, true,"1123");
    assertCwdsPrivileges("fixtures/jwtToken-test6.json", false,false, false, true, true, false,"1126");
  }

  private void assertCwdsPrivileges(String jsonFile, boolean isSocialWorkerOnly, boolean isCountySealed,
      boolean isCountySensitive, boolean isStateSealed, boolean isStateSensitive, boolean facilitiesRead, String countyId)
      throws IOException {
    CwdsPrivileges cwdsPrivileges = CwdsPrivileges.fromJson(fixture(jsonFile));
    assertEquals(isSocialWorkerOnly, cwdsPrivileges.isSocialWorkerOnly());
    assertEquals(isCountySealed, cwdsPrivileges.isCountySealed());
    assertEquals(isCountySensitive, cwdsPrivileges.isCountySensitive());
    assertEquals(isStateSealed, cwdsPrivileges.isStateSealed());
    assertEquals(isStateSensitive, cwdsPrivileges.isStateSensitive());
    assertEquals(countyId, cwdsPrivileges.getCountyId());
    assertEquals(facilitiesRead, cwdsPrivileges.isFacilitiesRead());
  }
}

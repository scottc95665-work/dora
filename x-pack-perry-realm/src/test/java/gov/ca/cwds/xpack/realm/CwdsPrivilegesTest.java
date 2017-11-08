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
    assertCwdsPrivileges("fixtures/jwtToken-FFFF.json", false, false, false, false, "1086");
    assertCwdsPrivileges("fixtures/jwtToken-FFFT.json", false, false, false, true, "1086");
    assertCwdsPrivileges("fixtures/jwtToken-FFTF.json", false, false, true, false, "1086");
    assertCwdsPrivileges("fixtures/jwtToken-FTFF.json", false, true, false, false, "1123");
    assertCwdsPrivileges("fixtures/jwtToken-TFFF.json", true, false, false, false, "1123");
    assertCwdsPrivileges("fixtures/jwtToken-TTTT.json", true, true, true, true, "1123");
  }

  private void assertCwdsPrivileges(String jsonFile, boolean isCountySealed,
      boolean isCountySensitive, boolean isStateSealed, boolean isStateSensitive, String countyId)
      throws IOException {
    CwdsPrivileges cwdsPrivileges = CwdsPrivileges.fromJson(fixture(jsonFile));
    assertEquals(isCountySealed, cwdsPrivileges.isCountySealed());
    assertEquals(isCountySensitive, cwdsPrivileges.isCountySensitive());
    assertEquals(isStateSealed, cwdsPrivileges.isStateSealed());
    assertEquals(isStateSensitive, cwdsPrivileges.isStateSensitive());
    assertEquals(countyId, cwdsPrivileges.getCountyId());
  }
}

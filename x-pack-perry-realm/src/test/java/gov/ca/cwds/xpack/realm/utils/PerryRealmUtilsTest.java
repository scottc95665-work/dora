package gov.ca.cwds.xpack.realm.utils;

import static gov.ca.cwds.xpack.realm.utils.PerryRealmUtils.parsePerryTokenFromJSON;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author CWDS TPT-2
 */
public class PerryRealmUtilsTest {

  private static final String INVALID_JSON = "test";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testInvalidParsePerryTokenFromJSON() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    parsePerryTokenFromJSON(INVALID_JSON);
  }
}

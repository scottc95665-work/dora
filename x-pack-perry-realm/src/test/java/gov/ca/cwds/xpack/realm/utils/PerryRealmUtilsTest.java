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
  private static final String EMPTY_JSON = "";
  private static final String CORRUPT_JSON = "{][sdfd{}";

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testParseInvalidJSON() throws Exception {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("JSON token is not valid");
    parsePerryTokenFromJSON(INVALID_JSON);
  }

  @Test
  public void testParseEmptyJSON() throws Exception {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("JSON token is empty");
    parsePerryTokenFromJSON(EMPTY_JSON);
  }

  @Test
  public void testParseCorruptJSON() throws Exception {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("JSON token is not valid");
    parsePerryTokenFromJSON(CORRUPT_JSON);
  }

  @Test
  public void testParseNull() throws Exception {
    exception.expect(IllegalArgumentException.class);
    exception.expectMessage("JSON token is null");
    parsePerryTokenFromJSON(null);
  }
}

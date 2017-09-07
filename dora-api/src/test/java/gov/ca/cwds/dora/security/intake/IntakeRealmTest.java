package gov.ca.cwds.dora.security.intake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import gov.ca.cwds.dora.security.DoraSecurityUtils;
import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class IntakeRealmTest {

  private IntakeRealm intakeRealm;

  @Before
  public void setUp() {
    intakeRealm = new IntakeRealm();
    intakeRealm.onInit();
  }

  @Test
  public void testIntakeRealm() throws IOException {
    IntakeAccount intakeAccount = intakeAccount(intakeRealm, "/security/intake/jwt_token_1.json");

    assertNotNull(intakeAccount);
    assertEquals("RACFID", intakeAccount.getUser());
    assertEquals("34", intakeAccount.getStaffId());
    assertEquals("Supervisor", intakeAccount.getRoles().iterator().next());
    assertEquals("19", intakeAccount.getCountyCode());
    assertEquals("Los Angeles", intakeAccount.getCountyName());

    assertNotNull(intakeAccount.getPrivileges());
    Iterator<String> privilegesIterator = intakeAccount.getPrivileges().iterator();

    assertEquals("Countywide Read", privilegesIterator.next());
    assertEquals("Sensitive Persons", privilegesIterator.next());
  }

  @Test
  public void testRunAsUser() throws IOException {
    IntakeAccount intakeAccount = intakeAccount(intakeRealm, "/security/intake/jwt_token_1.json");
    assertEquals("T.F.F.F.1086", DoraSecurityUtils.getElasticsearchRunAsUser(intakeAccount));

    intakeAccount = intakeAccount(intakeRealm, "/security/intake/jwt_token_2.json");
    assertEquals("F.T.F.F.1086", DoraSecurityUtils.getElasticsearchRunAsUser(intakeAccount));

    intakeAccount = intakeAccount(intakeRealm, "/security/intake/jwt_token_3.json");
    assertEquals("F.F.T.F.1086", DoraSecurityUtils.getElasticsearchRunAsUser(intakeAccount));

    intakeAccount = intakeAccount(intakeRealm, "/security/intake/jwt_token_4.json");
    assertEquals("F.F.F.T.1070", DoraSecurityUtils.getElasticsearchRunAsUser(intakeAccount));

    intakeAccount = intakeAccount(intakeRealm, "/security/intake/jwt_token_x.json");
    assertEquals("F.F.F.T.xxxx", DoraSecurityUtils.getElasticsearchRunAsUser(intakeAccount));
  }

  private static IntakeAccount intakeAccount(IntakeRealm intakeRealm, String resource)
      throws IOException {
    String json = IOUtils.toString(IntakeRealmTest.class.getResourceAsStream(resource), "UTF-8");
    return intakeRealm.map(json);
  }
}

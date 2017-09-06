package gov.ca.cwds.dora.security.intake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class IntakeRealmTest {

  @Test
  public void testIntakeRealm() throws IOException {
    IntakeRealm intakeRealm = new IntakeRealm();
    intakeRealm.onInit();

    String json = IOUtils
        .toString(IntakeRealmTest.class.getResourceAsStream("/security/intake/jwt_token.json"),
            "UTF-8");
    IntakeAccount intakeAccount = intakeRealm.map(json);

    assertNotNull(intakeAccount);
    assertEquals("RACFID", intakeAccount.getUser());
    assertEquals("34", intakeAccount.getStaffId());
    assertEquals("Supervisor", intakeAccount.getRoles().iterator().next());
    assertEquals("19", intakeAccount.getCountyCode());
    assertEquals("Los Angeles", intakeAccount.getCountyName());

    assertNotNull(intakeAccount.getPrivileges());
    Iterator<String> privilegesIterator = intakeAccount.getPrivileges().iterator();

    assertEquals("Statewide Read", privilegesIterator.next());
    assertEquals("Sensitive Persons", privilegesIterator.next());
  }
}

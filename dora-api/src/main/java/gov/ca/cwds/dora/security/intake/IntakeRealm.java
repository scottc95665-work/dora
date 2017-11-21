package gov.ca.cwds.dora.security.intake;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.security.realm.JwtRealm;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntakeRealm extends JwtRealm {

  private static final Logger LOGGER = LoggerFactory.getLogger(IntakeRealm.class);

  private ObjectMapper objectMapper;

  @Override
  protected void onInit() {
    super.onInit();
    objectMapper = new ObjectMapper();
  }

  @Override
  protected IntakeAccount map(String json) {
    try {
      return objectMapper.readValue(json, IntakeAccount.class);
    } catch (IOException e) {
      LOGGER.info(e.getMessage(), e);
      // Mapping doesn't apply
      IntakeAccount intakeAccount = new IntakeAccount();
      intakeAccount.setUser(json);
      return intakeAccount;
    }
  }
}

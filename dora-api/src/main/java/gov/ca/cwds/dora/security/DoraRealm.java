package gov.ca.cwds.dora.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.dora.security.intake.IntakeAccount;
import gov.ca.cwds.security.realm.PerryRealm;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoraRealm extends PerryRealm {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoraRealm.class);

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

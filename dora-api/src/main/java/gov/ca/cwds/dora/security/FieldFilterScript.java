package gov.ca.cwds.dora.security;

import gov.ca.cwds.dora.security.intake.IntakeAccount;
import gov.ca.cwds.script.MappingScript;
import groovy.json.JsonOutput;
import java.io.IOException;
import java.util.Map;
import javax.script.ScriptException;

/**
 * @author CWDS TPT-2
 */
public class FieldFilterScript extends MappingScript {

  /**
   * @param filePath script file location taken from property file
   * @throws IOException when can't read the filtering script
   */
  FieldFilterScript(String filePath) throws IOException {
    super(filePath, "response", "account");
  }

  public String filter(Map<String, Object> esResponseJsonMap, IntakeAccount intakeAccount)
      throws ScriptException {
    return JsonOutput.toJson(eval(esResponseJsonMap, intakeAccount));
  }
}

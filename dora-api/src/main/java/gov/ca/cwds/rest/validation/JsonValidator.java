package gov.ca.cwds.rest.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 8/2/2018.
 */
public class JsonValidator implements ConstraintValidator<ValidJson, String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonValidator.class);

  @Override
  public void initialize(ValidJson parameters) {
    //empty
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    try {
      new JSONObject(value);
    } catch (Exception e) {
      LOGGER.error("Invalid json ", e);
      return false;
    }
    return true;
  }

}

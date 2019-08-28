package gov.ca.cwds.rest.filters;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import gov.ca.cwds.auth.PerryUserIdentity;
import gov.ca.cwds.dora.security.intake.IntakeAccount;

/**
 * Used for unit tests only.
 */
public class TestingRequestExecutionContext implements RequestExecutionContext {

  /**
   * Context parameters
   */
  private Map<Parameter, Object> contextParameters = new HashMap<>();

  /**
   * constructor
   * 
   * @param userId frame user id
   */
  public TestingRequestExecutionContext(String userId) {
    final IntakeAccount userIdentity = new IntakeAccount();
    userIdentity.setUser(userId);
    userIdentity.setCountyCode("99");
    userIdentity.setCountyCwsCode("1126");

    put(Parameter.REQUEST_START_TIME, new Date());
    put(Parameter.USER_IDENTITY, userIdentity);

    RequestExecutionContextRegistry.register(this);
  }

  @Override
  public void put(Parameter parameter, Object value) {
    contextParameters.put(parameter, value);
  }

  @Override
  public Object get(Parameter parameter) {
    return contextParameters.get(parameter);
  }

  @Override
  public String getUserId() {
    String userId = null;
    PerryUserIdentity userIdentity = getUserIdentity();
    if (userIdentity != null) {
      userId = userIdentity.getUser();
    }
    return userId;
  }

  public PerryUserIdentity getUserIdentity() {
    return (PerryUserIdentity) get(Parameter.USER_IDENTITY);
  }

  @Override
  public Date getRequestStartTime() {
    return (Date) get(Parameter.REQUEST_START_TIME);
  }

  public String getStaffId() {
    String staffId = null;
    IntakeAccount userIdentity = (IntakeAccount) get(Parameter.USER_IDENTITY);
    if (userIdentity != null) {
      staffId = userIdentity.getUser();
    }
    return staffId;
  }

}

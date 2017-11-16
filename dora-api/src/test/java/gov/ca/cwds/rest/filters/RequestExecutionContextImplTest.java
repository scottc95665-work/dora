package gov.ca.cwds.rest.filters;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.auth.PerryUserIdentity;
import gov.ca.cwds.rest.filters.RequestExecutionContext.Parameter;
import java.util.Date;
import java.util.EnumMap;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.hamcrest.junit.ExpectedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;

/**
 * @author CWDS TPT-2
 */
public class RequestExecutionContextImplTest {

  private static final String TEST_STRING = "testString";
  private static final String CONTEXT_PARAMETERS = "contextParameters";
  private static final String USER = "testUser";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Spy
  @InjectMocks
  private RequestExecutionContextImpl requestExecutionContext; // "Class Under Test"

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void type() throws Exception {
    assertThat(RequestExecutionContextImpl.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(requestExecutionContext, notNullValue());
  }

  @Test
  public void testContextParameters() throws Exception {
    final EnumMap<Parameter, Object> mockContextParameters = new EnumMap<>(Parameter.class);

    Whitebox.setInternalState(requestExecutionContext, CONTEXT_PARAMETERS,
        mockContextParameters);

    assertNotNull(requestExecutionContext);

    requestExecutionContext.put(Parameter.USER_IDENTITY, TEST_STRING);
    Assert.assertEquals(TEST_STRING, requestExecutionContext.get(Parameter.USER_IDENTITY));
  }

  @Test
  public void testGetUserId() throws Exception {
    PerryUserIdentity userIdentity = new PerryUserIdentity();
    userIdentity.setUser(USER);

    requestExecutionContext.put(Parameter.USER_IDENTITY, userIdentity);
    Assert.assertEquals(USER, requestExecutionContext.getUserId());
  }


  @Test
  public void testStartRequest() throws Exception {
    thrown.expect(UnavailableSecurityManagerException.class);
    RequestExecutionContextImpl.startRequest();
  }

  @Test
  public void testRequestStartTime() throws Exception {
    Date startTime = new Date();
    requestExecutionContext.put(Parameter.REQUEST_START_TIME, startTime);
    Assert.assertNotNull(requestExecutionContext.get(Parameter.REQUEST_START_TIME));
    Assert.assertEquals(startTime, requestExecutionContext.getRequestStartTime());
  }

  @Test
  public void testStopRequest() throws Exception {
    RequestExecutionContextImpl.stopRequest();
    Assert.assertNull(RequestExecutionContextRegistry.get());
  }


  @Test
  public void tesRegisterRequestContext() throws Exception {
    RequestExecutionContext requestExecutionContext = Mockito.mock(RequestExecutionContext.class);
    RequestExecutionContextRegistry.register(requestExecutionContext);
    Assert.assertNotNull(RequestExecutionContextRegistry.get());
    RequestExecutionContextRegistry.remove();
    Assert.assertNull(RequestExecutionContextRegistry.get());
  }
}

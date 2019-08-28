package gov.ca.cwds.rest.filters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.hamcrest.junit.ExpectedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dora.security.intake.IntakeAccount;
import gov.ca.cwds.logging.LoggingContext;
import gov.ca.cwds.logging.MDCLoggingContext;

/**
 * @author CWDS TPT-2
 */
public class RequestResponseLoggingFilterTest extends AbstractShiroTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  ServletRequest request;

  @Mock
  ServletResponse response;

  @Mock
  FilterChain chain;

  @Mock
  FilterConfig filterConfig;

  LoggingContext loggingContext;

  @Spy
  @InjectMocks
  private RequestResponseLoggingFilter target; // "Class Under Test"

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    chain = mock(FilterChain.class);
    filterConfig = mock(FilterConfig.class);

    Subject mockSubject = mock(Subject.class);
    PrincipalCollection principalCollection = mock(PrincipalCollection.class);

    final String json =
        "{\"user\":\"STINKY\", \"staffId\":\"abc\", \"roles\":[\"CWS-admin\",\"Supervisor\"], \"county_code\":\"99\", \"county_cws_code\":\"1126\", \"county_name\":\"State of California\", \"privileges\": [ \"Statewide Read\", \"Create Service Provider\", \"CWS Case Management System\", \"Officewide Read/Write\", \"Resource Management\", \"Resource Mgmt Placement Facility Maint\", \"Sealed\", \"Sensitive Persons\", \"Snapshot-rollout\", \"Closed Case/Referral Update\", \"Hotline-rollout\", \"Facility-search-rollout\", \"RFA-rollout\", \"CANS-rollout\", \"development-not-in-use\"] }";
    final IntakeAccount intakeAccount = new ObjectMapper().readValue(json, IntakeAccount.class);

    List<Object> list = new ArrayList<>();
    list.add("msg");
    list.add(intakeAccount);

    when(principalCollection.asList()).thenReturn(list);
    when(mockSubject.getPrincipals()).thenReturn(principalCollection);
    setSubject(mockSubject);

    final ServletInputStream sis = new DelegateServletInputStream();
    when(request.getInputStream()).thenReturn(sis);

    loggingContext = new MDCLoggingContext();
    loggingContext.initialize();
    target = new RequestResponseLoggingFilter(loggingContext);

    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) {
        final RequestExecutionContext ctx = RequestExecutionContext.instance();
        System.out.println(ctx);
        final String userId = ctx.getUserId();

        assertThat(userId, is(equalTo("STINKY")));
        return null;
      }
    }).when(chain).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
  }

  @Test
  public void testDoFilterHappyPath() throws Exception {
    target.doFilter(request, response, chain);
  }

}

package gov.ca.cwds.rest.filters;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
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

import gov.ca.cwds.logging.LoggingContext;
import gov.ca.cwds.logging.LoggingContext.LogParameter;
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

    List<String> list = new ArrayList<>();
    list.add("msg");

    when(principalCollection.asList()).thenReturn(list);
    when(mockSubject.getPrincipals()).thenReturn(principalCollection);
    setSubject(mockSubject);

    final ServletInputStream sis = new DelegateServletInputStream();
    when(request.getInputStream()).thenReturn(sis);

    loggingContext = new MDCLoggingContext();
    new TestingRequestExecutionContext("MORGOTH");

    target = new RequestResponseLoggingFilter(loggingContext);
    RequestExecutionContextImpl.startRequest();
  }

  @Test
  public void type() throws Exception {
    assertThat(RequestResponseLoggingFilter.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void testDoFilterHappyPath() throws Exception {
    final String uniqueId = "MORGOTH";

    // doReturn(uniqueId).when(loggingContext).initialize();
    target.doFilter(request, response, chain);
    final String userId = loggingContext.getLogParameter(LogParameter.USER_ID);
    System.out.println(userId);
  }

}

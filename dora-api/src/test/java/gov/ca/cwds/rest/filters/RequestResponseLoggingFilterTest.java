package gov.ca.cwds.rest.filters;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.UnavailableSecurityManagerException;
import org.hamcrest.junit.ExpectedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import gov.ca.cwds.logging.LoggingContext;

/**
 * @author CWDS TPT-2
 */
public class RequestResponseLoggingFilterTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private LoggingContext loggingContext;

  @Spy
  @InjectMocks
  private RequestResponseLoggingFilter loggingFilter; // "Class Under Test"

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void type() throws Exception {
    assertThat(RequestResponseLoggingFilter.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(loggingFilter, notNullValue());
  }

  @Test(expected = UnavailableSecurityManagerException.class)
  public void testDoFilterHappyPath() throws Exception {
    String uniqueId = "testUniqueId";

    final ServletRequest request =
        mock(ServletRequest.class, withSettings().extraInterfaces(HttpServletRequest.class));
    final ServletResponse response = mock(HttpServletResponse.class);
    final FilterChain chain = mock(FilterChain.class);

    doReturn(uniqueId).when(loggingContext).initialize();
    loggingFilter.doFilter(request, response, chain);
  }

  @Test
  public void testDoFilterSnapshotQuery() throws Exception {
    String uniqueId = "testUniqueId";

    final ServletRequest request =
        mock(ServletRequest.class, withSettings().extraInterfaces(HttpServletRequest.class));
    final ServletResponse response = mock(HttpServletResponse.class);
    final FilterChain chain = mock(FilterChain.class);

    doReturn(uniqueId).when(loggingContext).initialize();

    thrown.expect(UnavailableSecurityManagerException.class);
    loggingFilter.doFilter(request, response, chain);
  }

}

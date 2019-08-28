package gov.ca.cwds.rest.filters;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
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

  @Mock
  private LoggingContext loggingContext;

  @Spy
  @InjectMocks
  private RequestResponseLoggingFilter loggingFilter; // "Class Under Test"

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

    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
        "{\"size\":\"250\",\"track_scores\":\"true\",\"sort\":[{\"_score\":\"desc\",\"last_name.keyword\":\"asc\",\"first_name.keyword\":\"asc\",\"_uid\":\"desc\"}],\"min_score\":\"2.5\",\"_source\":[\"id\",\"legacy_source_table\",\"first_name\",\"middle_name\",\"last_name\",\"name_suffix\",\"gender\",\"akas\",\"date_of_birth\",\"date_of_death\",\"ssn\",\"languages\",\"races\",\"ethnicity\",\"client_counties\",\"case_status\",\"addresses.id\",\"addresses.effective_start_date\",\"addresses.street_name\",\"addresses.street_number\",\"addresses.city\",\"addresses.county\",\"addresses.state_code\",\"addresses.zip\",\"addresses.type\",\"addresses.legacy_descriptor\",\"addresses.phone_numbers.number\",\"addresses.phone_numbers.type\",\"csec.start_date\",\"csec.end_date\",\"csec.csec_code_id\",\"csec.description\",\"sp_county\",\"sp_phone\",\"legacy_descriptor\",\"highlight\",\"phone_numbers.id\",\"phone_numbers.number\",\"phone_numbers.type\",\"estimated_dob_code\",\"sensitivity_indicator\",\"race_ethnicity\",\"open_case_responsible_agency_code\"],\"highlight\":{\"order\":\"score\",\"number_of_fragments\":\"10\",\"require_field_match\":\"false\",\"fields\":{\"autocomplete_search_bar\":{\"matched_fields\":[\"autocomplete_search_bar\",\"autocomplete_search_bar.phonetic\",\"autocomplete_search_bar.diminutive\"]},\"searchable_date_of_birth\":{}}},\"query\":{\"bool\":{\"must\":[{\"match\":{\"legacy_descriptor.legacy_ui_id_flat\":{\"query\":\"1406607661170082074\",\"boost\":\"14\"}}}]}}}"
            .getBytes(Charset.defaultCharset()));

    final ServletInputStream sis = mock(ServletInputStream.class);
    when(sis.read()).thenReturn(byteArrayInputStream.read());
    when(sis.isFinished()).thenReturn(false);
    when(sis.isReady()).thenReturn(true);
    when(request.getInputStream()).thenReturn(sis);

    new TestingRequestExecutionContext("MORGOTH");
    RequestExecutionContextImpl.startRequest();
  }

  @Test
  public void type() throws Exception {
    assertThat(RequestResponseLoggingFilter.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(loggingFilter, notNullValue());
  }

  @Test
  public void testDoFilterHappyPath() throws Exception {
    String uniqueId = "MORGOTH";

    doReturn(uniqueId).when(loggingContext).initialize();
    loggingFilter.doFilter(request, response, chain);
  }

}

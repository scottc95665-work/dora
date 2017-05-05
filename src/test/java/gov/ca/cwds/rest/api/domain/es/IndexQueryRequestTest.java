package gov.ca.cwds.rest.api.domain.es;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import gov.ca.cwds.rest.resources.cms.JerseyGuiceRule;
import io.dropwizard.jackson.Jackson;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.hamcrest.junit.ExpectedException;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;

/**
 * Test domain request class, {@link IndexQueryRequest}.
 * 
 * <p>
 * NOTE: Mockito cannot mock up or spy on final classes, like String, and thereby that framework
 * cannot inject test artifacts into test objects.
 * </p>
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class IndexQueryRequestTest {

  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  @Rule
  public ExpectedException thrown = ExpectedException.none();


  @After
  public void ensureServiceLocatorPopulated() {
    JerseyGuiceUtils.reset();
  }

  @ClassRule
  public static JerseyGuiceRule rule = new JerseyGuiceRule();

  @Before
  public void setup() {}

  @Test
  public void type() throws Exception {
    assertThat(IndexQueryRequest.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    IndexQueryRequest target = produce("index", null);
    assertThat(target, notNullValue());
  }

  @Test
  public void equalsHashCodeWork() throws Exception {
    EqualsVerifier.forClass(IndexQueryRequest.class).suppress(Warning.NONFINAL_FIELDS).verify();
  }

  @Test
  public void equals_Args$Object() throws Exception {
    IndexQueryRequest target = produce("index", null);
    Object obj = null;
    final boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  protected IndexQueryRequest produce(String index, String s) {
    return new IndexQueryRequest(index, s);
  }

}

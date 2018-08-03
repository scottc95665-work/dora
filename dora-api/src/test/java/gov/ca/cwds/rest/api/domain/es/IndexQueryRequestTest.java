package gov.ca.cwds.rest.api.domain.es;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest.IndexQueryRequestBuilder;
import gov.ca.cwds.rest.resources.JerseyGuiceRule;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.junit.ExpectedException;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test domain request class, {@link IndexQueryRequest}.
 * <p>
 * NOTE: Mockito cannot mock up or spy on final classes, like String, and thereby that framework
 * cannot inject test artifacts into test objects.
 * </p>
 *
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class IndexQueryRequestTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @After
  public void ensureServiceLocatorPopulated() {
    JerseyGuiceUtils.reset();
  }

  @ClassRule
  public static JerseyGuiceRule rule = new JerseyGuiceRule();

  @Before
  public void setup() {
  }

  @Test
  public void type() throws Exception {
    assertThat(IndexQueryRequest.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    IndexQueryRequest target = produce("type", null);
    assertThat(target, notNullValue());
  }

  @Test
  public void equalsHashCodeWork() throws Exception {
    EqualsVerifier.forClass(IndexQueryRequest.class).suppress(Warning.NONFINAL_FIELDS).verify();
  }

  private IndexQueryRequest produce(String type, String s) {
    return new IndexQueryRequestBuilder().addDocumentType(type).addRequestBody(s).build();
  }

}

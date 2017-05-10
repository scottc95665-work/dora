package gov.ca.cwds.rest.api.domain.es;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.rest.resources.JerseyGuiceRule;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.hamcrest.junit.ExpectedException;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;

/**
 * Test domain request class, {@link IndexQueryResponse}.
 * <p>
 * <p>
 * NOTE: Mockito cannot mock up or spy on final classes, like String, and thereby that framework
 * cannot inject test artifacts into test objects.
 * </p>
 *
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class IndexQueryResponseTest {

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
        assertThat(IndexQueryResponse.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        IndexQueryResponse target = produce(null);
        assertThat(target, notNullValue());
    }

    @Test
    public void equalsHashCodeWork() throws Exception {
        EqualsVerifier.forClass(IndexQueryResponse.class)// .suppress(Warning.NONFINAL_FIELDS).verify();
                .suppress(Warning.NONFINAL_FIELDS, Warning.STRICT_INHERITANCE).suppress().verify();
    }

    private IndexQueryResponse produce(String s) {
        return new IndexQueryResponse(s);
    }

}

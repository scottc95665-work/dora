package gov.ca.cwds.rest.services.es;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.junit.ExpectedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class IndexQueryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private IndexQueryRequest req;

    @Mock
    private ElasticsearchConfiguration esConfig;

    @Spy
    @InjectMocks
    private IndexQueryService target; // "Class Under Test"

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void type() throws Exception {
        assertThat(IndexQueryService.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        assertThat(target, notNullValue());
    }

    @Test
    public void testHandleRequest() throws Exception {
        Map<String, String> test = new HashMap<>();
        test.put("a", "value");
        req = new IndexQueryRequest("people", "person", test);
        esConfig = new ElasticsearchConfiguration("localhost", "9200");

        Whitebox.setInternalState(target, "esConfig", esConfig);
        doReturn("fred").when(target)
                .searchIndexByQuery(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        final IndexQueryResponse actual = target.handleRequest(req);
        IndexQueryResponse expected = new IndexQueryResponse("fred");

        assertThat(actual, is(equalTo(expected)));
    }

}

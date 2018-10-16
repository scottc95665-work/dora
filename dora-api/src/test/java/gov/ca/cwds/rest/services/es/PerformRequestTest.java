package gov.ca.cwds.rest.services.es;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import gov.ca.cwds.managed.EsRestClientManager;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration.XpackConfiguration;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest.IndexQueryRequestBuilder;
import gov.ca.cwds.security.realm.PerrySubject;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EsRestClientManager.class, PerrySubject.class})
public class PerformRequestTest {

  @Test
  public void testPerformRequest() throws IOException {
    IndexQueryService target = new IndexQueryService();

    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    esConfig.setUser("user");
    esConfig.setPassword("password");
    esConfig.setNodes("localhost:1");
    Whitebox.setInternalState(target, "esConfig", esConfig);

    Response response = mock(Response.class);
    RestClient mockRestClient = mock(RestClient.class);
    doReturn(response).when(mockRestClient).performRequest(any(Request.class));

    mockStatic(EsRestClientManager.class);
    when(EsRestClientManager.getEsRestClient()).thenReturn(mockRestClient);

    assertNotNull(target.performRequest(prepareIndexQueryRequest()));
  }

  @Test
  public void testPerformRequestWithXpack() throws IOException {
    IndexQueryService target = new IndexQueryService();

    mockStatic(PerrySubject.class);
    PowerMockito.when(PerrySubject.getToken()).thenReturn("");

    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    esConfig.setUser("user");
    esConfig.setPassword("password");
    esConfig.setNodes("localhost:1");
    XpackConfiguration xpackConfiguration = new XpackConfiguration();
    xpackConfiguration.setEnabled(true);
    esConfig.setXpack(xpackConfiguration);
    Whitebox.setInternalState(target, "esConfig", esConfig);

    Response response = mock(Response.class);
    RestClient mockRestClient = mock(RestClient.class);
    doReturn(response).when(mockRestClient).performRequest(any(Request.class));

    mockStatic(EsRestClientManager.class);
    when(EsRestClientManager.getEsRestClient()).thenReturn(mockRestClient);

    assertNotNull(target.performRequest(prepareIndexQueryRequest()));
  }

  private IndexQueryRequest prepareIndexQueryRequest() {
    return new IndexQueryRequestBuilder().addRequestBody("{}").addDocumentType("person")
        .addEsEndpoint("/people/person/_search").build();
  }

}

package gov.ca.cwds.dora.health;

import static gov.ca.cwds.rest.DoraConstants.Index.AUDIT_EVENTS_ES_ALIAS_ENDPOINT;
import static gov.ca.cwds.rest.DoraConstants.Index.AUDIT_EVENTS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Index.FACILITIES_CWS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Index.FACILITIES_ES_ALIAS_ENDPOINT;
import static gov.ca.cwds.rest.DoraConstants.Index.FACILITIES_LIS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Index.USERS_ES_ALIAS_ENDPOINT;
import static gov.ca.cwds.rest.DoraConstants.Index.USERS_INDEX;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codahale.metrics.health.HealthCheck;
import gov.ca.cwds.rest.DoraConfiguration;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.http.entity.BasicHttpEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class ElasticsearchAliasWithIndexHealthCheckTest {

  @Mock DoraConfiguration config;
  @Mock RestClient esRestClient;
  @Spy @InjectMocks ElasticsearchAliasWithIndexHealthCheck elasticsearchAliasWithIndexHealthCheck;

  public static final String FACILITIES_ALIASES_RESPONSE =
      "{\"facilities-cws_1558581697772\":{\"aliases\":{\"facilities\":{}}},\"facilities-lis_1558582981901\":{\"aliases\":{\"facilities\":{}}}}";
  public static final String INVALID_FACILITIES_ALIASES_RESPONSE =
      "{\"facilities\":{\"aliases\":{}}}";
  public static final String USERS_ALIASES_RESPONSE =
      "{\"users_1558555798269\":{\"aliases\":{\"users\":{}}}}";
  public static final String AUDIT_EVENTS_ALIASES_RESPONSE =
      "{\"auditevents_1557793976348\":{\"aliases\":{\"auditevents\":{}}}}";

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testTrueHealthStatusGivenFacilitiesCwsIndexWithTimeStamp() throws IOException {
    initMock(FACILITIES_CWS_INDEX, FACILITIES_ES_ALIAS_ENDPOINT, FACILITIES_ALIASES_RESPONSE);

    final HealthCheck.Result health =
        elasticsearchAliasWithIndexHealthCheck.elasticsearchCheck(esRestClient);
    assertThat(health.getMessage(), is("[facilities-cws] timestamp exists on the server."));
    assertThat(health.isHealthy(), is(TRUE));
  }

  @Test
  public void testTrueHealthStatusGivenFacilitiesLisIndexWithTimeStamp() throws IOException {
    initMock(FACILITIES_LIS_INDEX, FACILITIES_ES_ALIAS_ENDPOINT, FACILITIES_ALIASES_RESPONSE);

    final HealthCheck.Result health =
        elasticsearchAliasWithIndexHealthCheck.elasticsearchCheck(esRestClient);
    assertThat(health.getMessage(), is("[facilities-lis] timestamp exists on the server."));
    assertThat(health.isHealthy(), is(TRUE));
  }

  @Test
  public void testTrueHealthStatusGivenAuditEventsIndexWithTimeStamp() throws IOException {
    initMock(AUDIT_EVENTS_INDEX, AUDIT_EVENTS_ES_ALIAS_ENDPOINT, AUDIT_EVENTS_ALIASES_RESPONSE);

    final HealthCheck.Result health =
        elasticsearchAliasWithIndexHealthCheck.elasticsearchCheck(esRestClient);
    assertThat(health.getMessage(), is("[auditevents] timestamp exists on the server."));
    assertThat(health.isHealthy(), is(TRUE));
  }

  @Test
  public void testTrueHealthStatusGivenUsersIndexWithTimeStamp() throws IOException {
    initMock(USERS_INDEX, USERS_ES_ALIAS_ENDPOINT, USERS_ALIASES_RESPONSE);

    final HealthCheck.Result health =
        elasticsearchAliasWithIndexHealthCheck.elasticsearchCheck(esRestClient);
    assertThat(health.getMessage(), is("[users] timestamp exists on the server."));
    assertThat(health.isHealthy(), is(TRUE));
  }

  @Test
  public void testFalseHealthStatusGivenInvalidIndexWithTimeStamp() throws IOException {
    initMock(
        FACILITIES_CWS_INDEX, FACILITIES_ES_ALIAS_ENDPOINT, INVALID_FACILITIES_ALIASES_RESPONSE);

    final HealthCheck.Result health =
        elasticsearchAliasWithIndexHealthCheck.elasticsearchCheck(esRestClient);
    assertThat(health.getMessage(), is("[facilities-cws] timestamp does not exist on the server."));
  }

  @Test
  public void toArrayTest() {
    final String[] facResponsesArray =
        elasticsearchAliasWithIndexHealthCheck.toArray(FACILITIES_ALIASES_RESPONSE);
    assertThat(facResponsesArray.length, is(6));
    String[] expectedValues = {
      "facilities-cws_1558581697772",
      "aliases",
      "facilities",
      "facilities-lis_1558582981901",
      "aliases",
      "facilities"
    };
    assertThat(facResponsesArray, equalTo(expectedValues));
  }

  @Test
  public void toArrayNullTest() {
    final String[] facResponsesArray = elasticsearchAliasWithIndexHealthCheck.toArray(null);
    assertThat(facResponsesArray.length, is(0));
    String[] expectedValues = new String[0];
    assertThat(facResponsesArray, equalTo(expectedValues));
  }

  @Test
  public void toArrayInvalidStringTest() {
    final String[] facResponsesArray = elasticsearchAliasWithIndexHealthCheck.toArray("test");
    assertThat(facResponsesArray.length, is(0));
    String[] expectedValues = new String[0];
    assertThat(facResponsesArray, equalTo(expectedValues));
  }

  private void initMock(String index, String aliasEndpoint, String aliasesResponse)
      throws IOException {
    when(elasticsearchAliasWithIndexHealthCheck.getIndexName()).thenReturn(index);
    when(elasticsearchAliasWithIndexHealthCheck.getAliasEndpoint()).thenReturn(aliasEndpoint);

    Response response = mock(Response.class);
    BasicHttpEntity entity = new BasicHttpEntity();
    entity.setContent(new ByteArrayInputStream(aliasesResponse.getBytes()));
    when(response.getEntity()).thenReturn(entity);

    doReturn(response).when(esRestClient).performRequest("GET", aliasEndpoint);
  }
}

package gov.ca.cwds.rest.services.es;

import static gov.ca.cwds.dora.DoraUtils.getElasticsearchSearchResultCount;
import static gov.ca.cwds.dora.DoraUtils.getElasticsearchSearchTime;
import static gov.ca.cwds.dora.DoraUtils.stringToJsonMap;
import static gov.ca.cwds.dora.security.BasicAuthRealm.EXTERNAL_APP_PRINCIPAL;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.shiro.SecurityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.dora.security.FieldFilterScript;
import gov.ca.cwds.dora.security.FieldFilters;
import gov.ca.cwds.dora.security.intake.IntakeAccount;
import gov.ca.cwds.managed.EsRestClientManager;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.ElasticsearchException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.security.realm.PerrySubject;

/**
 * Business service for Index Query.
 *
 * @author CWDS TPT-2
 */
public class IndexQueryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexQueryService.class);

  @Inject
  private ElasticsearchConfiguration esConfig;

  @Inject
  private FieldFilters fieldFilters;

  /**
   * Constructor
   */
  public IndexQueryService() {
    // no op
  }

  public IndexQueryResponse handleRequest(IndexQueryRequest request) {
    try {
      long timeBeforeCallES = System.currentTimeMillis();
      Response response = performRequest(request);
      LOGGER.debug("Dora took {} milliseconds to call Elasticsearch",
          System.currentTimeMillis() - timeBeforeCallES);

      try (InputStream content = response.getEntity().getContent()) {
        String esResponse = IOUtils.toString(content, UTF_8.toString());
        Map<String, Object> esResponseJsonMap = stringToJsonMap(esResponse);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Elastic Search took {} milliseconds to execute the search",
              getElasticsearchSearchTime(esResponseJsonMap));
          LOGGER.debug("Elastic Search has {} results in total",
              getElasticsearchSearchResultCount(esResponseJsonMap));
        }
        if (fieldFilters.hasFilter(request.getDocumentType())) {
          LOGGER.debug("Applying field filtering for document type '{}'",
              request.getDocumentType());
          return new IndexQueryResponse(
              applyFieldFiltering(esResponseJsonMap, request.getDocumentType()));
        } else {
          LOGGER.debug("Field filtering for document type '{}' is not defined",
              request.getDocumentType());
          return new IndexQueryResponse(esResponse);
        }
      }
    } catch (ResponseException e) {
      throw new ElasticsearchException(e);
    } catch (IOException e) {
      throw new DoraException("Failed to call ES", e);
    }
  }

  String applyFieldFiltering(Map<String, Object> esResponseJsonMap, String documentType) {
    FieldFilterScript fieldFilterScript = fieldFilters.getFilter(documentType);
    IntakeAccount account = PerrySubject.getPerryAccount();
    try {
      return fieldFilterScript.filter(esResponseJsonMap, account);
    } catch (ScriptException e) {
      throw new DoraException(
          "Failed to apply Field Filtering for document type '" + documentType + "'", e);
    }
  }

  Response performRequest(IndexQueryRequest request) throws IOException {
    InputStreamEntity entity =
        new InputStreamEntity(new ByteArrayInputStream(request.getRequestBody().getBytes(UTF_8)));
    RestClient esRestClient = EsRestClientManager.getEsRestClient();
    Request esRequest = new Request(request.getHttpMethod(), request.getEsEndpoint());
    if (request.getParameters() != null) {
      request.getParameters().forEach((key, value) -> esRequest.addParameter(key, value));
    }
    esRequest.setEntity(entity);
    boolean isExternalApplication = isExternalApplication();
    if (!isExternalApplication && esConfig.getXpack() != null && esConfig.getXpack().isEnabled()) {
      esRequest.setOptions(buildRequestOptions());
      return esRestClient.performRequest(esRequest);
    } else {
      esRequest.setOptions(addContentType());
      return esRestClient.performRequest(esRequest);
    }
  }

  private RequestOptions buildRequestOptions() {
    final RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
    builder.addHeader("Authorization", PerrySubject.getToken());
    builder.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
    return builder.build();
  }

  private RequestOptions addContentType() {
    final RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
    builder.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
    return builder.build();
  }

  private boolean isExternalApplication() {
    final Object principal = SecurityUtils.getSubject().getPrincipal();
    return EXTERNAL_APP_PRINCIPAL.equals(principal);
  }

}

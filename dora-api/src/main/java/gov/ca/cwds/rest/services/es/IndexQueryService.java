package gov.ca.cwds.rest.services.es;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import gov.ca.cwds.dora.security.DoraSecurityUtils;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Business service for Index Query.
 *
 * @author CWDS API Team
 */
public class IndexQueryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexQueryService.class);

  private static final String REQUEST_METHOD_GET = "GET";
  private static final String REQUEST_PROPERTY_CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_JSON = "application/json";

  private ElasticsearchConfiguration esConfig;

  /**
   * Constructor
   *
   * @param esConfig instance of ElasticsearchConfiguration with host and port assigned
   */
  @Inject
  public IndexQueryService(ElasticsearchConfiguration esConfig) {
    this.esConfig = esConfig;
  }

  public IndexQueryResponse handleRequest(IndexQueryRequest req) {
    checkArgument(req != null, "query cannot be Null or empty");
    @SuppressWarnings("unchecked")
    String query = new JSONObject((Map<String, String>) req.getQuery()).toString();
    if (StringUtils.isBlank(query)) {
      LOGGER.error("query cannot be null.");
      throw new DoraException("query cannot be null.");
    }
    return new IndexQueryResponse(searchIndexByQuery(req.getIndex(), req.getType(), query));
  }

  /**
   * Search given index by pass-through query.
   *
   * @param index index to search
   * @param type  "person" or otherwise
   * @param query user-provided query
   * @return JSON ES results
   */
  String searchIndexByQuery(final String index, final String type, final String query) {
    LOGGER.warn(" index: {}", index);
    LOGGER.warn(" type: {}", type);
    LOGGER.warn(" QUERY: {}", query);
    checkArgument(!Strings.isNullOrEmpty(index), "index name cannot be Null or empty");
    checkArgument(!Strings.isNullOrEmpty(type), "type cannot be Null or empty");
    checkArgument(!Strings.isNullOrEmpty(query), "query cannot be Null or empty");

    StringBuilder sb = new StringBuilder();
    sb.append("http://").append(esConfig.getHost().trim()).append(':')
            .append(esConfig.getPort()).append('/').append(index).append('/').append(type.trim())
            .append("/_search");
    final String targetURL = sb.toString();
    LOGGER.warn("ES SEARCH URL: {}", targetURL);
    return executionResult(targetURL, query);
  }

  /**
   * Consume an external REST web service, specifying URL, request headers and JSON payload
   *
   * @param targetURL the target URL
   * @param payload   the payload specified by user
   * @return the JSON payload returned by the external web service
   */
  String executionResult(String targetURL, String payload) {
    HttpURLConnection connection = null;

    try {
      connection = createConnection(targetURL);
      DoraSecurityUtils.applySecurity(connection, esConfig);
      if (StringUtils.isNotEmpty(payload)) {
        String query = payload.trim();
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),
                StandardCharsets.UTF_8);
        writer.write(query);
        writer.close();
      }

      if (connection.getResponseCode() == 200) {
        return IOUtils.toString(connection.getInputStream());
      } else {
        throw new DoraException(IOUtils.toString(connection.getErrorStream()));
      }
    } catch (IOException e) {
      throw new DoraException(e.getMessage(), e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  HttpURLConnection createConnection(String targetURL)
          throws IOException {
    URL url = new URL(targetURL);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.setRequestMethod(REQUEST_METHOD_GET);
    connection.setRequestProperty(REQUEST_PROPERTY_CONTENT_TYPE, APPLICATION_JSON);
    return connection;
  }
}

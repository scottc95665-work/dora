package gov.ca.cwds.rest.services.es;

import static com.google.common.base.Preconditions.checkArgument;

import gov.ca.cwds.rest.api.DoraException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import gov.ca.cwds.auth.realms.PerryAccount;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;


/**
 * Business service for Index Query.
 *
 * @author CWDS API Team
 */
public class IndexQueryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexQueryService.class);

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
   * @param type "person" or otherwise
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
   * @param payload the payload specified by user
   * @return the JSON payload returned by the external web service
   */
  String executionResult(String targetURL, String payload) {
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    StringBuilder jsonString = new StringBuilder();

    try {
      URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/json");
      applySecurity(connection);
      if (StringUtils.isNotEmpty(payload)) {
        String query = payload.trim();
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF8");
        writer.write(query);
        writer.close();
      }
      reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
      String line;
      while ((line = reader.readLine()) != null) {
        jsonString.append(line);
      }
    } catch (IOException | RuntimeException e) {
      final String msg = "Error in ElasticSearch: " + e.getMessage();
      LOGGER.error(msg, e);
      throw new DoraException(msg, e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          final String msg = "Error in ElasticSearch: " + e.getMessage();
          LOGGER.error(msg, e);
        }
      }
      if (connection != null) {
        connection.disconnect();
      }
    }

    return jsonString.toString();
  }

  private void applySecurity(HttpURLConnection connection) throws UnsupportedEncodingException {
    if (esConfig.getXpack() != null) {
      ElasticsearchConfiguration.XpackConfiguration xpackConfiguration = esConfig.getXpack();
      if (xpackConfiguration.isEnabled()) {
        setAuthorizationHeader(connection);
        setRunAsuser(connection);
      }
    }
  }

  private void setRunAsuser(HttpURLConnection connection) {
    String runAsUser = getElasticsearchRunAsUser();
    if (runAsUser != null) {
      connection.setRequestProperty("es-security-runas-user", runAsUser);
    }
  }

  private void setAuthorizationHeader(HttpURLConnection connection)
      throws UnsupportedEncodingException {
    String name = esConfig.getXpack().getUser();
    String password = esConfig.getXpack().getPassword();

    String authString = name + ":" + password;
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes("UTF-8"));
    String authStringEnc = new String(authEncBytes, "UTF-8");
    connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
  }

  private String getElasticsearchRunAsUser() {
    Subject subject = SecurityUtils.getSubject();
    if (subject != null) {
      List principals = subject.getPrincipals().asList();
      if (principals.size() == 2) {
        PerryAccount account = (PerryAccount) principals.get(1);
        if (account.getRoles() != null) {
          if (!account.getRoles().isEmpty()) {
            return account.getRoles().iterator().next();
          }
        }
      }
    }
    return null;
  }
}

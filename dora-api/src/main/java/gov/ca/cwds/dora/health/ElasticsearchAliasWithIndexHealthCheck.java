package gov.ca.cwds.dora.health;

import com.google.inject.Inject;
import gov.ca.cwds.rest.DoraConfiguration;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author CWDS TPT-2 */
public class ElasticsearchAliasWithIndexHealthCheck extends ElasticsearchIndexHealthCheck {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ElasticsearchAliasWithIndexHealthCheck.class);

  static final String HEALTHY_TIMESTAMP_INDEX_MSG = "[%s] timestamp exists on the server.";
  static final String UNHEALTHY_TIMESTAMP_INDEX_MSG =
      "[%s] timestamp does not exist on the server.";
  Pattern indexTimestampPattern = Pattern.compile(".+[_]\\d{13}");
  public static final String DOUBLE_QUOTE_ESCAPE_CHAR = "\"";
  public static final String ALIASES = "aliases";
  public static final String SEPARATOR = "-";

  private String aliasEndpoint;

  /**
   * Constructor.
   *
   * @param config instance of DoraConfiguration.
   * @param aliasEndpoint alias end point of indexes.
   */
  @Inject
  public ElasticsearchAliasWithIndexHealthCheck(
      DoraConfiguration config, String indexName, String aliasEndpoint) {
    super(config, indexName);
    this.aliasEndpoint = aliasEndpoint;
  }

  @Override
  protected Result elasticsearchCheck(RestClient esRestClient) throws IOException {
    String[] esAliasAttributes = getEsAliasAttributes(esRestClient);
    if (checkIndexWithTimeStampExists(esAliasAttributes)) {
      String healthyMsg = String.format(HEALTHY_TIMESTAMP_INDEX_MSG, getIndexName());
      LOGGER.info(healthyMsg);
      return Result.healthy(healthyMsg);
    } else {
      String unhealthyMsg = String.format(UNHEALTHY_TIMESTAMP_INDEX_MSG, getIndexName());
      LOGGER.error(unhealthyMsg);
      return Result.unhealthy(unhealthyMsg);
    }
  }

  protected String[] getEsAliasAttributes(RestClient esRestClient) throws IOException {
    Response response = esRestClient.performRequest("GET", getAliasEndpoint());
    if (response == null || response.getEntity().getContent() == null) {
      return new String[0];
    }
    String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
    return toArray(content);
  }

  protected String[] toArray(String content) {
    if (content == null || !content.contains(DOUBLE_QUOTE_ESCAPE_CHAR)) {
      return new String[0];
    }
    return StringUtils.substringsBetween(
        content, DOUBLE_QUOTE_ESCAPE_CHAR, DOUBLE_QUOTE_ESCAPE_CHAR);
  }

  protected boolean checkIndexWithTimeStampExists(String[] esAttributes) {
    return checkCondition(esAttributes, this::containsWordAliases)
        && checkCondition(esAttributes, this::containsAliasItself)
        && checkCondition(esAttributes, this::containsIndexWithTimestamp);
  }

  private boolean containsWordAliases(String esAttribute) {
    return esAttribute.equals(ALIASES);
  }

  private boolean containsAliasItself(String esAttribute) {
    return esAttribute.equals(StringUtils.substringBefore(getIndexName(), SEPARATOR));
  }

  private boolean containsIndexWithTimestamp(String esAttribute) {
    return esAttribute.contains(getIndexName())
        && indexTimestampPattern.matcher(esAttribute).matches();
  }

  private static boolean checkCondition(String[] esAttributes, Predicate<String> condition) {
    return Arrays.stream(esAttributes).anyMatch(condition);
  }

  public String getAliasEndpoint() {
    return aliasEndpoint;
  }
}

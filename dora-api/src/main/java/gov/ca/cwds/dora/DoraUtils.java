package gov.ca.cwds.dora;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Stream;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

/**
 * @author CWDS TPT-2
 */
public final class DoraUtils {

  private static final String SYS_INFO_PROPERTIES_FILE = "system-information.properties";
  private static final String BUILD_VERSION = "build.version";

  private DoraUtils() {
    // no op
  }

  public static RestClient createElasticsearchClient(ElasticsearchConfiguration esConfig) {
    if (esConfig.getXpack().isEnabled()) {
      // build authorized ES REST client
      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY,
          new UsernamePasswordCredentials(esConfig.getXpack().getUser(),
              esConfig.getXpack().getPassword()));

      return RestClient
          .builder(new HttpHost(esConfig.getHost(), Integer.parseInt(esConfig.getPort())))
          .setHttpClientConfigCallback(
              httpClientBuilder -> httpClientBuilder
                  .setDefaultCredentialsProvider(credentialsProvider))
          .build();
    } else {
      // build anonymous ES REST client
      return RestClient
          .builder(new HttpHost(esConfig.getHost(), Integer.parseInt(esConfig.getPort()))).build();
    }
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> stringToJsonMap(String jsonString) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(jsonString, Map.class);
    } catch (IOException e) {
      throw new DoraException("failed to parse json string", e);
    }
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> responseToJsonMap(Response response) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(response.getEntity().getContent(), Map.class);
  }

  @SuppressWarnings("unchecked")
  public static String extractElasticsearchVersion(Map<String, Object> jsonMap) {
    Map<String, Object> version = (Map<String, Object>) jsonMap.get("version");
    return version.get("number").toString();
  }

  @SuppressWarnings("unchecked")
  public static Integer getElasticSearchSearchResultCount(Map<String, Object> jsonMap) {
    return (Integer) ((Map<String, Object>)jsonMap.get("hits")).get("total");
  }

  @SuppressWarnings("unchecked")
  public static Integer getElasticSearchSearchTime(Map<String, Object> jsonMap) {
    return (Integer) jsonMap.get("took");
  }

  public static String extractElasticsearchClusterName(Map<String, Object> jsonMap) {
    return jsonMap.get("cluster_name").toString();
  }

  @SuppressWarnings("unchecked")
  public static Stream<String> extractNodesWithoutPlugin(Map<String, Object> jsonMap,
      String pluginName) {
    Map<String, Object> nodesMap = (Map<String, Object>) jsonMap.get("nodes");

    // looking for nodes that does not have phonetic search plugin installed
    return nodesMap.entrySet().stream().filter(esNodeEntry -> {
      Map<String, Object> esNode = (Map<String, Object>) esNodeEntry.getValue();
      List<Object> esNodePluginsList = (List<Object>) esNode.get("plugins");
      // return true if the node does not have the plugin, because we need to have such nodes after the filter
      return esNodePluginsList.stream().noneMatch(
          plugin -> ((Map<String, Object>) plugin).get("name").equals(pluginName)
      );
    }).map(Entry::getKey);
  }

  private static Properties getSystemInformationProperties() {
    Properties versionProperties = new Properties();
    try {
      InputStream is = ClassLoader.getSystemResourceAsStream(SYS_INFO_PROPERTIES_FILE);
      versionProperties.load(is);
    } catch (IOException e) {
      throw new DoraException("Can't read " + SYS_INFO_PROPERTIES_FILE, e);
    }
    return versionProperties;
  }

  public static String getAppVersion() {
    return getSystemInformationProperties().getProperty(BUILD_VERSION);
  }

  public static String escapeCRLF(String str) {
    return null != str ? str.replaceAll("[\r\n]", "") : null;
  }
}

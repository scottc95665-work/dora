package gov.ca.cwds.dora;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.security.realm.PerrySubject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Stream;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

/**
 * @author CWDS TPT-2
 */
public final class DoraUtils {

  private static final String SYS_INFO_PROPERTIES_FILE = "system-information.properties";
  private static final String BUILD_VERSION = "build.version";

  private DoraUtils() {
    // no op
  }

  public static HttpHost[] parseNodes(String nodesValue) {
    String[] nodes = nodesValue.split(",");
    HttpHost[] hosts = new HttpHost[nodes.length];
    for (int i = 0; i < nodes.length; i++) {
      String node = nodes[i];
      String[] hostPortPair = node.split(":");
      String host = getHost(hostPortPair);
      int port = getPort(hostPortPair);
      hosts[i] = new HttpHost(host, port);
    }
    return hosts;
  }

  @SuppressWarnings("fb-contrib:CLI_CONSTANT_LIST_INDEX")
  private static int getPort(String[] hostPortPair) {
    return hostPortPair.length > 1 && hostPortPair[1] != null ?
        Integer.parseInt(hostPortPair[1]) : -1;
  }

  private static String getHost(String[] hostPortPair) {
    return hostPortPair.length > 0 ? hostPortPair[0] : "";
  }

  public static RestClient createXpackElasticsearchClient(
      ElasticsearchConfiguration esConfig) {
    HttpHost[] httpHosts = parseNodes(esConfig.getNodes());
    Header[] headers = new Header[1];
    headers[0] = new BasicHeader("Authorization", PerrySubject.getToken());
    RestClientBuilder restClientBuilder = RestClient.builder(httpHosts).setDefaultHeaders(headers);
    return restClientBuilder.build();
  }

  public static RestClient createElasticsearchClient(ElasticsearchConfiguration esConfig) {
    HttpHost[] httpHosts = parseNodes(esConfig.getNodes());
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY,
        new UsernamePasswordCredentials(esConfig.getUser(),
            esConfig.getPassword()));

    RestClientBuilder restClientBuilder = RestClient
        .builder(httpHosts)
        .setHttpClientConfigCallback(
            httpClientBuilder -> httpClientBuilder
                .setDefaultCredentialsProvider(credentialsProvider));
    return restClientBuilder.build();
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
  public static List<Object> responseToList(Response response) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(response.getEntity().getContent(), List.class);
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
    return (Integer) ((Map<String, Object>) jsonMap.get("hits")).get("total");
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

  @SuppressWarnings("unchecked")
  public static boolean isIndexExist(List<Object> jsonList,
      String indexName) {

    return jsonList.stream().anyMatch(
        index -> ((Map<String, Object>) index).get("index").equals(indexName)
    );
  }

  private static Properties getSystemInformationProperties() {
    Properties versionProperties = new Properties();
    try {
      InputStream is = ClassLoader.getSystemResourceAsStream(SYS_INFO_PROPERTIES_FILE);
      if (is != null) {
        versionProperties.load(is);
      }
    } catch (Exception e) {
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

package gov.ca.cwds.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author CWDS Elasticsearch Team
 */
public class ElasticsearchConfiguration {
    @JsonProperty("host")
    private String host;

    @JsonProperty("port")
    private String port;

    public ElasticsearchConfiguration() {
        // no op
    }

    public ElasticsearchConfiguration(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }
}

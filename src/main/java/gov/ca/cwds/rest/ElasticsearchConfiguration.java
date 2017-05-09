package gov.ca.cwds.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * @author CWDS Elasticsearch Team
 */
public class ElasticsearchConfiguration {
    @NotNull
    @JsonProperty("host")
    private String host;

    @NotNull
    @JsonProperty("port")
    private String port;

    @NotNull
    @JsonProperty("cluster")
    private String cluster;

    public ElasticsearchConfiguration() {
        // no op
    }

    public ElasticsearchConfiguration(String host, String port, String cluster) {
        this.host = host;
        this.port = port;
        this.cluster = cluster;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getCluster() {
        return cluster;
    }
}

package gov.ca.cwds.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author CWDS Elasticsearch Team
 */
public class ElasticsearchConfiguration {
    @NotEmpty
    @JsonProperty("host")
    private String host;

    @NotEmpty
    @JsonProperty("port")
    private String port;

    @NotEmpty
    @JsonProperty
    private Map<String, String> responseFieldFilters;

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

    public Map<String, String> getResponseFieldFilters() {
        return responseFieldFilters;
    }

    public static class XpackConfiguration {
        private boolean enabled;
        private String user;
        private String password;

        public boolean isEnabled() {
            return enabled;
        }

        @JsonProperty
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUser() {
            return user;
        }

        @JsonProperty
        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        @JsonProperty
        public void setPassword(String password) {
            this.password = password;
        }
    }

    private XpackConfiguration xpack;

    public XpackConfiguration getXpack() {
        return xpack;
    }

    @JsonProperty
    public void setXpack(XpackConfiguration xpack) {
        this.xpack = xpack;
    }
}

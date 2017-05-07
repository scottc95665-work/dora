package gov.ca.cwds.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import gov.ca.cwds.rest.ApiConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.ApiException;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * @author CWDS Elasticsearch Team
 */
public class ElasticsearchAccessModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchAccessModule.class);

    private Client esClient;

    @Override
    protected void configure() {
        // no op
    }

    @Provides
    public Client elasticsearchClient(ApiConfiguration apiConfiguration) {
        if (esClient == null) {
            ElasticsearchConfiguration config = apiConfiguration.getElasticsearchConfiguration();
            try {
                Settings settings = Settings.settingsBuilder()
                        .put("cluster.name", config.getElasticsearchCluster()).build();
                esClient = TransportClient.builder().settings(settings).build().addTransportAddress(
                        new InetSocketTransportAddress(InetAddress.getByName(config.getElasticsearchHost()),
                                Integer.parseInt(config.getElasticsearchPort())));
            } catch (Exception e) {
                LOGGER.error("Error initializing Elasticsearch esClient: {}", e.getMessage(), e);
                throw new ApiException("Error initializing Elasticsearch esClient: " + e.getMessage(), e);
            }
        }

        return esClient;
    }
}

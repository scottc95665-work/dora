package gov.ca.cwds.rest.services.es;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.google.common.base.Strings;
import gov.ca.cwds.data.es.ApiElasticSearchException;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.rest.resources.SimpleResourceService;
import gov.ca.cwds.rest.services.ServiceException;


/**
 * Business service for Intake Index Query.
 *
 * @author CWDS API Team
 */
public class IndexQueryService
        extends SimpleResourceService<String, IndexQueryRequest, IndexQueryResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexQueryService.class);

    private ElasticsearchConfiguration esConfig;

    /**
     * Constructor
     *
     */
    @Inject
    public IndexQueryService(ElasticsearchConfiguration esConfig) {
        this.esConfig = esConfig;
    }

    @Override
    protected IndexQueryResponse handleRequest(IndexQueryRequest req) {
        checkArgument(req != null, "query cannot be Null or empty");
        @SuppressWarnings("unchecked")
        String query = new JSONObject((Map<String, String>) req.getQuery()).toString();
        if (StringUtils.isBlank(query)) {
            LOGGER.error("query cannot be null.");
            throw new ServiceException("query cannot be null.");
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
    public String searchIndexByQuery(final String index, final String type, final String query) {
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
    private String executionResult(String targetURL, String payload) {
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
            if (StringUtils.isNotEmpty(payload)) {
                String query = payload.trim();
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF8");
                writer.write(query);
                writer.close();
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        } catch (Exception e) {
            final String msg = "Error in ElasticSearch: " + e.getMessage();
            LOGGER.error(msg, e);
            throw new ApiElasticSearchException(msg, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
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

    @Override
    protected IndexQueryResponse handleFind(String s) {
        throw new IllegalStateException("this method is not applicable to Elasticsearch");
    }
}

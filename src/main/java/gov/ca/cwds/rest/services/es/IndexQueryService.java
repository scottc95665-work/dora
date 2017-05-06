package gov.ca.cwds.rest.services.es;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.es.ElasticsearchDao;
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

    private ElasticsearchDao elasticsearchDao;

    /**
     * Constructor
     *
     * @param elasticsearchDao the ElasticSearch DAO
     */
    @Inject
    public IndexQueryService(ElasticsearchDao elasticsearchDao) {
        this.elasticsearchDao = elasticsearchDao;
    }

    /**
     * Consolidate calls to Elasticsearch DAO in one place.
     *
     * @param index the name of index to search
     * @param query the elasticsearch query
     * @return complete domain object
     */
    String callDao(final String index, final String query) {
        return this.elasticsearchDao.searchIndexByQuery(index, query);
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
        return new IndexQueryResponse(callDao(req.getIndex(), query));
    }

    @Override
    protected IndexQueryResponse handleFind(String searchForThis) {
        try {
            return new IndexQueryResponse(
                    callDao(elasticsearchDao.getDefaultAlias(), searchForThis.trim()));
        } catch (Exception e) {
            LOGGER.error("Something went wrong ...", e.getMessage());
            throw new ServiceException("Something went wrong ...", e);
        }
    }

}

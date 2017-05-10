package gov.ca.cwds.rest.api.domain.es;

import gov.ca.cwds.rest.api.Request;
import gov.ca.cwds.rest.api.Response;
import io.dropwizard.jackson.JsonSnakeCase;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * A domain API {@link Response} from Index Query service.
 * <p>
 * Contains JSON string of a response returned from Elasticsearch REST API.
 * </p>
 *
 * @author CWDS API Team
 */
@ApiModel
@JsonSnakeCase
public class IndexQueryResponse implements Serializable, Response {

    /**
     * Base serialization version. Increment by class version.
     */
    private static final long serialVersionUID = 2L;

    @JsonRawValue
    private String searchResults;

    /**
     * Disallow use of default constructor.
     */
    @SuppressWarnings("unused")
    private IndexQueryResponse() {
        // Default, no-op.
    }

    /**
     * Preferred constructor
     *
     * @param searchResults the json response from Elasticsearch
     */
    public IndexQueryResponse(String searchResults) {
        this.searchResults = searchResults;
    }

    /**
     * Getter for Elasticsearch response
     *
     * @return Elasticsearch json response
     */
    @JsonRawValue
    public String getSearchResults() {
        return searchResults;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, false);
    }

}

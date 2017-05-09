package gov.ca.cwds.rest.api.domain.es;

import gov.ca.cwds.rest.api.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A domain API {@link Request} for Intake Index Query feature to Elasticsearch.
 * <p>
 * The Intake Index Query for an Index takes an index name and a json as string, which is used to
 * query the Elasticsearch Index documents by ALL relevant fields that are specified in the query.
 * </p>
 *
 * @author CWDS API Team
 */
@ApiModel
public class IndexQueryRequest implements Serializable, Request {

    /**
     * Base serialization version. Increment by class version.
     */
    private static final long serialVersionUID = 2L;

    @ApiModelProperty(required = true, example = "a valid elasticsearch index name")
    @JsonProperty("index")
    private String index;

    @ApiModelProperty(required = true, example = "a valid elasticsearch document type")
    @JsonProperty("type")
    private String type;

    @ApiModelProperty(required = true, example = "a valid elasticsearch query json")
    @JsonProperty("query")
    private transient Object query;

    /**
     * JSON DropWizard Constructor. Takes query.
     *
     * @param index the name of the elasticsearch index to search
     * @param query the elasticsearch query
     */
    @JsonCreator
    public IndexQueryRequest(
            @NotNull @JsonProperty("index") String index,
            @NotNull @JsonProperty("type") String type,
            @Valid @NotNull @JsonProperty("query") Object query
    ) {
        this.index = index;
        this.type = type;
        this.query = query;
    }

    /**
     * @return the index
     */
    public String getIndex() {
        return index;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Getter for query.
     *
     * @return query the elasticsearch query
     */
    public Object getQuery() {
        return query;
    }

    @Override
    public final int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public final boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, false);
    }

}

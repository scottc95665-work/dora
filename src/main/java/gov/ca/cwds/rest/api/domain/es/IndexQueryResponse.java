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
 * A domain API {@link Request} for Intake Index Query feature to Elasticsearch.
 * 
 * <p>
 * The Intake Index Query for an Index takes an Elasticsearch Index name and single query as json
 * string, which is used to query the Elasticsearch Index documents by ALL relevant fields that are
 * specified in the query.
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
  private static final long serialVersionUID = 1L;

  @JsonRawValue
  private String persons;

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
   * @param persons the json response from Elasticsearch
   */
  public IndexQueryResponse(String persons) {
    this.persons = persons;
  }

  /**
   * Getter for Elasticsearch response
   * 
   * @return Elasticsearch json response
   */
  @JsonRawValue
  public String getPersons() {
    return persons;
  }

  /**
   * Setter for Elasticsearch json response
   * 
   * @param persons a json string
   */
  public void setPersons(String persons) {
    this.persons = persons;
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

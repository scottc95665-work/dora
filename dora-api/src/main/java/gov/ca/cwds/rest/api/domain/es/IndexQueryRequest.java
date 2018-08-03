package gov.ca.cwds.rest.api.domain.es;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import javax.ws.rs.HttpMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A domain API Request for Index Query feature to Elasticsearch. <p> The Index Query for an Index
 * takes an index name, document type, and a json as string, which is used to requestBody the
 * Elasticsearch Index documents by ALL relevant fields that are specified in the requestBody. </p>
 *
 * @author CWDS API Team
 */
@ApiModel
public final class IndexQueryRequest implements Serializable {

  /**
   * Base serialization version. Increment by class version.
   */
  private static final long serialVersionUID = 3L;

  private IndexQueryRequest() {}

  /**
   * Http method for elasticsearch call
   */
  private String httpMethod;
  private String requestBody;
  private String documentType;
  private String endpoint;

  public String getRequestBody() {
    return requestBody;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public String getDocumentType() {
    return documentType;
  }

  public String getEndpoint() {
    return endpoint;
  }

  @Override
  public final int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public final boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

  public static class IndexQueryRequestBuilder {

    private String requestBody;
    private String httpMethod;
    private String documentType;
    private String endpoint;


    public IndexQueryRequestBuilder addEndpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    public IndexQueryRequestBuilder addDocumentType(String documentType) {
      this.documentType = documentType;
      return this;
    }

    public IndexQueryRequestBuilder addRequestBody(String requestBody) {
      this.requestBody = requestBody;
      return this;
    }

    public IndexQueryRequestBuilder addHttpMethod(String httpMethod) {
      this.httpMethod = httpMethod;
      return this;
    }

    public IndexQueryRequest build() {
      IndexQueryRequest request = new IndexQueryRequest();
      request.endpoint = endpoint;
      request.documentType = documentType;
      request.requestBody = requestBody;
      if (StringUtils.isNotBlank(httpMethod)) {
        request.httpMethod = httpMethod;
      } else {
        request.httpMethod = HttpMethod.POST;
      }
      return request;
    }

  }

}

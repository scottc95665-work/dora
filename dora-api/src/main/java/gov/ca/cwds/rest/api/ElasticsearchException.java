package gov.ca.cwds.rest.api;

import javax.ws.rs.core.Response;
import gov.ca.cwds.rest.exception.ExpectedException;
import org.elasticsearch.client.ResponseException;

public class ElasticsearchException extends ExpectedException {
  public ElasticsearchException(ResponseException e) {
    super(e.getMessage(), Response.Status.fromStatusCode(e.getResponse().getStatusLine().getStatusCode()));
  }
}

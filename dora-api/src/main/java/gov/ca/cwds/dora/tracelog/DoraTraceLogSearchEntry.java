package gov.ca.cwds.dora.tracelog;

import java.time.LocalDateTime;

import gov.ca.cwds.data.std.ApiObjectIdentity;

/**
 * Represents a user's search query. Used by asynchronous Trace Log service.
 * 
 * @author CWDS API Team
 */
public class DoraTraceLogSearchEntry extends ApiObjectIdentity {

  private static final long serialVersionUID = 1L;

  private final String index;
  private final String userId;
  private final String json;
  private final LocalDateTime moment = LocalDateTime.now();

  public DoraTraceLogSearchEntry(String userId, String index, String json) {
    this.index = index;
    this.userId = userId;
    this.json = json;
  }

  public String getIndex() {
    return index;
  }

  public String getUserId() {
    return userId;
  }

  public LocalDateTime getMoment() {
    return moment;
  }

  public String getJson() {
    return json;
  }

  @Override
  public String toString() {
    return "DoraTraceLogSearchEntry [index=" + index + ", userId=" + userId + ", json=" + json
        + ", moment=" + moment + "]";
  }

}

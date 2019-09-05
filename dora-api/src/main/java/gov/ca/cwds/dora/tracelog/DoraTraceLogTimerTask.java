package gov.ca.cwds.dora.tracelog;

import java.util.Queue;
import java.util.TimerTask;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.rest.DoraConfiguration;

/**
 * Asynchronous task sends search queries to the Trace Log system.
 * 
 * @author CWDS API Team
 */
public class DoraTraceLogTimerTask extends TimerTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoraTraceLogTimerTask.class);

  private final Client client;
  private final Queue<DoraTraceLogSearchEntry> searchQueue;
  private final String traceLogUrl;

  @Inject
  public DoraTraceLogTimerTask(DoraConfiguration config, Client client,
      Queue<DoraTraceLogSearchEntry> searchQueue) {
    LOGGER.warn("\n\n******* CREATE DoraTraceLogTimerTask: id: {} ********\n", this);
    this.client = client;
    this.searchQueue = searchQueue;
    this.traceLogUrl = config.getTraceLogUrl();
  }

  protected void sendSearchQuery(DoraTraceLogSearchEntry entry) {
    final String url = traceLogUrl + "/" + entry.getUserId() + "/" + entry.getIndex();
    final Response response = client.target(url).request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(entry.getJson(), MediaType.APPLICATION_JSON));

    // Future option. Pun intended.
    // final Future<Response> futureResponse =
    // client.target(url).request(MediaType.APPLICATION_JSON)
    // .async().post(Entity.entity(entry.getJson(), MediaType.APPLICATION_JSON));

    final int status = response.getStatus();
    if (status == Status.CREATED.getStatusCode()) {
      final String json = response.readEntity(String.class);
      LOGGER.debug("Trace Log response: {}", json);
    } else {
      LOGGER.warn("FAILED TO CALL TRACE LOG! status {}", status);
    }
  }

  @Override
  public void run() {
    LOGGER.trace("Trace Log: flush search queue");

    if (searchQueue.isEmpty()) {
      return;
    }

    DoraTraceLogSearchEntry entry = null;
    try {
      while (!searchQueue.isEmpty() && (entry = searchQueue.poll()) != null) {
        LOGGER.debug("Trace Log: save search query: {}", entry);
        sendSearchQuery(entry);
      }
    } catch (Exception e) {
      LOGGER.error("ERROR SAVING SEARCH QUERY!", e);
      throw e;
    }
  }

}

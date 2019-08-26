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

public class DoraTraceLogTimerTask extends TimerTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoraTraceLogTimerTask.class);

  private static final String TRACE_LOG_URL =
      "https://ferbapi.integration.cwds.io/search_query?token=10dcf2c2-261e-45a2-a7c5-e3b6842d6858#";

  private final Client client;
  private final Queue<DoraTraceLogSearchEntry> searchQueue;

  @Inject
  public DoraTraceLogTimerTask(Client client, Queue<DoraTraceLogSearchEntry> searchQueue) {
    this.client = client;
    this.searchQueue = searchQueue;
  }

  protected void sendSearchQuery(DoraTraceLogSearchEntry entry) {
    final Response response = client.target(TRACE_LOG_URL).request(MediaType.APPLICATION_JSON)
        // .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
        .post(Entity.entity(entry.getJson(), MediaType.APPLICATION_JSON));

    final String json = response.readEntity(String.class);
    LOGGER.info("Trace Log response: {}", json);

    if (response.getStatus() != Status.OK.getStatusCode()) {
      LOGGER.warn("FAILED TO CALL FERB! status {}", response.getStatus());
    }
  }

  @Override
  public void run() {
    LOGGER.info("Trace Log: flush search queue");

    if (searchQueue.isEmpty()) {
      return;
    }

    DoraTraceLogSearchEntry entry = null;
    try {
      while (!searchQueue.isEmpty() && (entry = searchQueue.poll()) != null) {
        LOGGER.info("Trace Log: save search query: {}", entry);
        sendSearchQuery(entry);
      }
    } catch (Exception e) {
      LOGGER.error("ERROR SAVING SEARCH QUERY!", e);
      throw e;
    }
  }

}

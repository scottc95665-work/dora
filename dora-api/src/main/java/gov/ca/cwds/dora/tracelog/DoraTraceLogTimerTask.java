package gov.ca.cwds.dora.tracelog;

import java.util.Queue;
import java.util.TimerTask;

import javax.ws.rs.client.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class DoraTraceLogTimerTask extends TimerTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoraTraceLogTimerTask.class);

  private final Client client;
  private final Queue<DoraTraceLogSearchEntry> searchQueue;

  @Inject
  public DoraTraceLogTimerTask(Client client, Queue<DoraTraceLogSearchEntry> searchQueue) {
    this.client = client;
    this.searchQueue = searchQueue;
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
        LOGGER.trace("Trace Log: save search query: {}", entry);
        // TODO: send search query to Ferb.
      }
    } catch (Exception e) {
      LOGGER.error("ERROR SAVING BULK SEARCH QUERY!", e);
      throw e;
    }
  }

}

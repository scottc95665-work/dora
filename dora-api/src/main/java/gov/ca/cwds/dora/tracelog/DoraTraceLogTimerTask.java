package gov.ca.cwds.dora.tracelog;

import java.util.Queue;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoraTraceLogTimerTask extends TimerTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoraTraceLogTimerTask.class);

  private final Queue<DoraTraceLogSearchEntry> searchQueue;

  public DoraTraceLogTimerTask(Queue<DoraTraceLogSearchEntry> searchQueue) {
    this.searchQueue = searchQueue;
  }

  @Override
  public void run() {
    LOGGER.trace("Trace Log: flush queues");
    // searchDao.logBulkAccess(searchQueue);
  }

  public void logBulkAccess(Queue<DoraTraceLogSearchEntry> searchQueue) {
    if (searchQueue.isEmpty()) {
      return;
    }

    DoraTraceLogSearchEntry entry = null;
    try {
      while (!searchQueue.isEmpty() && (entry = searchQueue.poll()) != null) {
        LOGGER.debug("Trace Log: save search query: {}", entry);
        // TODO: send search query to Ferb.
      }
    } catch (Exception e) {
      LOGGER.error("ERROR SAVING BULK SEARCH QUERY!", e);
      throw e;
    }
  }

}

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

}

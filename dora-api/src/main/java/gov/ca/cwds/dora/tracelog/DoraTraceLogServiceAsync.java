package gov.ca.cwds.dora.tracelog;

import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.inject.Inject;

/**
 * Houses the asynchronous Dora Trace Log service.
 * 
 * <p>
 * Implements asynchronous service by means of thread-safe queue for search queries. Timer and timer
 * task consume queues asynchronously and call Ferb to insert into Postgres Trace Log tables.
 * </p>
 * 
 * @author CWDS API Team
 * @see TraceLogService
 */
public class DoraTraceLogServiceAsync implements DoraTraceLogService {

  protected final Queue<DoraTraceLogSearchEntry> searchQueue = new ConcurrentLinkedQueue<>();

  protected final Timer timer;

  @Inject
  public DoraTraceLogServiceAsync(long startDelay, long recurringDelay) {
    this.timer = new Timer("tracelog");
    timer.schedule(new DoraTraceLogTimerTask(searchQueue), startDelay, recurringDelay);
  }

  @Override
  public void logSearchQuery(String userId, String index, String json) {
    searchQueue.add(new DoraTraceLogSearchEntry(userId, index, json));
  }

}

package gov.ca.cwds.dora.tracelog;

import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.ws.rs.client.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger LOGGER = LoggerFactory.getLogger(DoraTraceLogServiceAsync.class);

  protected final Queue<DoraTraceLogSearchEntry> searchQueue = new ConcurrentLinkedQueue<>();

  protected final Timer timer;

  @Inject
  public DoraTraceLogServiceAsync(Client client) {
    this.timer = new Timer("tracelog");
    timer.schedule(new DoraTraceLogTimerTask(client, searchQueue), 20000L, 5000L);
  }

  @Override
  public void logSearchQuery(String userId, String index, String json) {
    LOGGER.info("Trace Log: queue ES query");
    searchQueue.add(new DoraTraceLogSearchEntry(userId, index, json));
  }

}

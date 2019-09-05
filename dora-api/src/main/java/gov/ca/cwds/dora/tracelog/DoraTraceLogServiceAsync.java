package gov.ca.cwds.dora.tracelog;

import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.ws.rs.client.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gov.ca.cwds.rest.DoraConfiguration;

/**
 * Houses the asynchronous Dora Trace Log service.
 * 
 * <p>
 * Implements asynchronous service by means of thread-safe queue for search queries. Timer and timer
 * task consume queues asynchronously and call Ferb to insert into Postgres Trace Log tables.
 * </p>
 * 
 * @author CWDS API Team
 */
@Singleton
public class DoraTraceLogServiceAsync implements DoraTraceLogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoraTraceLogServiceAsync.class);

  private static final long START_DELAY = 4000L;
  private static final long PERIODIC_DELAY = 2000L;

  protected final Queue<DoraTraceLogSearchEntry> searchQueue = new ConcurrentLinkedQueue<>();

  protected final Timer timer;

  @Inject
  public DoraTraceLogServiceAsync(DoraConfiguration config, Client client) {
    this.timer = new Timer("tracelog", true); // daemon thread
    timer.scheduleAtFixedRate(new DoraTraceLogTimerTask(config, client, searchQueue), START_DELAY,
        PERIODIC_DELAY); // will run concurrent catch-up threads, if needed
  }

  @Override
  public void logSearchQuery(String userId, String index, String json) {
    LOGGER.info("Trace Log: queue search query. user: {}, index: {}", userId, index);
    searchQueue.add(new DoraTraceLogSearchEntry(userId, index, json));
  }

}

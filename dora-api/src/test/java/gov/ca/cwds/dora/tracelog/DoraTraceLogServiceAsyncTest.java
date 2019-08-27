package gov.ca.cwds.dora.tracelog;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class DoraTraceLogServiceAsyncTest extends DoraTraceLogTimerTaskTest {

  DoraTraceLogServiceAsync target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new DoraTraceLogServiceAsync(config, client);
  }

  @Test
  public void type() throws Exception {
    assertThat(DoraTraceLogServiceAsync.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void logSearchQuery_A$String$String$String() throws Exception {
    String userId = "SAURON";
    String index = "people-summary";
    target.logSearchQuery(userId, index, json);
  }

}

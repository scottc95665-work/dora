package gov.ca.cwds.rest;

import static gov.ca.cwds.rest.DoraConstants.DEV_MODE;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.health.HealthCheckRegistry;

import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;

/**
 * @author CWDS TPT-2
 */
public class DoraApplicationTest {

  private DoraApplication tgt;

  @Before
  public void setup() throws Exception {
    tgt = new DoraApplication();
  }

  @Test
  public void type() {
    assertThat(DoraApplication.class, notNullValue());
  }

  @Test
  public void testRunInternal() {
    final DoraConfiguration config = mock(DoraConfiguration.class);
    final ElasticsearchConfiguration esConfig = mock(ElasticsearchConfiguration.class);
    when(config.getElasticsearchConfiguration()).thenReturn(esConfig);
    when(esConfig.getNodes()).thenReturn("localhost:9200");
    when(esConfig.getUser()).thenReturn("elastic");
    when(esConfig.getPassword()).thenReturn("changeme");

    final LifecycleEnvironment lifecycleEnv = mock(LifecycleEnvironment.class);
    final Environment env = mock(Environment.class);
    when(env.lifecycle()).thenReturn(lifecycleEnv);

    final HealthCheckRegistry health = mock(HealthCheckRegistry.class);
    when(env.healthChecks()).thenReturn(health);

    tgt.runInternal(config, env);

    Assert.assertEquals("DEV", DEV_MODE);
  }

}

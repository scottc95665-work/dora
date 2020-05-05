package gov.ca.cwds.rest;

import static gov.ca.cwds.rest.DoraConstants.DEV_MODE;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.GuiceBundle;

import gov.ca.cwds.rest.filters.RequestResponseLoggingFilter;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
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

    final JerseyEnvironment jersey = mock(JerseyEnvironment.class);
    when(env.jersey()).thenReturn(jersey);

    final ServletEnvironment servlets = mock(ServletEnvironment.class);
    when(env.servlets()).thenReturn(servlets);

    final FilterRegistration.Dynamic dynamic = mock(FilterRegistration.Dynamic.class);
    when(servlets.addFilter(any(String.class), any(Filter.class))).thenReturn(dynamic);
    when(servlets.addFilter("CORS", CrossOriginFilter.class)).thenReturn(dynamic);

    final HealthCheckRegistry health = mock(HealthCheckRegistry.class);
    when(env.healthChecks()).thenReturn(health);

    final GuiceBundle<DoraConfiguration> bundle = mock(GuiceBundle.class);
    final Injector injector = mock(Injector.class);
    when(bundle.getInjector()).thenReturn(injector);

    final RequestResponseLoggingFilter filter = mock(RequestResponseLoggingFilter.class);
    when(injector.getInstance(RequestResponseLoggingFilter.class)).thenReturn(filter);

    tgt.setGuiceBundle(bundle);
    tgt.runInternal(config, env);

    Assert.assertEquals("DEV", DEV_MODE);
  }

}

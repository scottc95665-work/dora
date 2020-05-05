package gov.ca.cwds.rest;

import static gov.ca.cwds.rest.DoraConstants.PROD_MODE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ES_CONFIG;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ES_STATUS;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_FACILITIES_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SEALED_NO_COUNTY_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SEALED_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SENSITIVE_NO_COUNTY_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SENSITIVE_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SUMMARY_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SUMMARY_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PHONETIC_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_X_PACK_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.Index.FACILITIES_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Index.PEOPLE_SUMMARY_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Plugin.PHONETIC_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.Plugin.X_PACK_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_SEALED_NO_COUNTY_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_SEALED_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_SENSITIVE_NO_COUNTY_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_SENSITIVE_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_SUMMARY_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.PEOPLE_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.Role.WORKER_ROLE;

import java.util.EnumSet;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.secnod.shiro.jaxrs.ShiroExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.hubspot.dropwizard.guice.GuiceBundle;

import gov.ca.cwds.dora.DoraUtils;
import gov.ca.cwds.dora.health.BasicDoraHealthCheck;
import gov.ca.cwds.dora.health.ElasticsearchHealthCheck;
import gov.ca.cwds.dora.health.ElasticsearchIndexHealthCheck;
import gov.ca.cwds.dora.health.ElasticsearchPluginHealthCheck;
import gov.ca.cwds.dora.health.ElasticsearchRolesHealthCheck;
import gov.ca.cwds.inject.ApplicationModule;
import gov.ca.cwds.managed.EsRestClientManager;
import gov.ca.cwds.rest.filters.RequestResponseLoggingFilter;
import gov.ca.cwds.rest.resources.SwaggerResource;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;

/**
 * Core execution class of CWDS REST Dora server application.
 * <h3>Standard command line arguments:</h3> <blockquote> server config/dora.yml </blockquote>
 * <h3>Standard JVM arguments:</h3>
 * <blockquote>-Djava.library.path=${workspace_loc:CWDS_API}/lib:/usr/local/lib/ </blockquote>
 *
 * @author CWDS API Team
 */
public final class DoraApplication extends BaseApiApplication<DoraConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoraApplication.class);

  /**
   * Start the CWDS RESTful API application.
   *
   * @param args command line
   */
  public static void main(final String[] args) {
    try {
      new DoraApplication().run(args);
    } catch (Exception e) {
      LOGGER.error("ERROR: {}", e.getMessage(), e);
      System.exit(1);
    }
  }

  @Override
  public Module applicationModule(Bootstrap<DoraConfiguration> bootstrap) {
    return new ApplicationModule();
  }

  @Override
  @SuppressWarnings("findsecbugs:CRLF_INJECTION_LOGS")
  // DoraConfiguration and system-information.properties are trusted sources
  public final void runInternal(final DoraConfiguration config, final Environment env) {
    final EsRestClientManager esRestClientManager =
        new EsRestClientManager(config.getElasticsearchConfiguration());
    env.lifecycle().manage(esRestClientManager);

    // register and run application health checks
    registerHealthChecks(config, env);
    runHealthChecks(env);
    final Injector injector = guiceBundle.getInjector();

    env.jersey().register(new ShiroExceptionMapper());
    env.servlets().setSessionHandler(new SessionHandler());

    env.servlets()
        .addFilter("AuditAndLoggingFilter",
            injector.getInstance(RequestResponseLoggingFilter.class))
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

    LOGGER.info("Application name: {}, Version: {}", config.getApplicationName(),
        DoraUtils.getAppVersion());

    LOGGER.info("Configuring CORS: Cross-Origin Resource Sharing");
    configureCors(env);

    LOGGER.info("Configuring SWAGGER");
    configureSwagger(config, env);
  }

  private static void configureCors(final Environment env) {
    final FilterRegistration.Dynamic filter =
        env.servlets().addFilter("CORS", CrossOriginFilter.class);
    filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
    filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
    filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
    filter.setInitParameter("allowedHeaders",
        "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,X-Auth-Token");
    filter.setInitParameter("allowCredentials", "true");
  }

  private void configureSwagger(final DoraConfiguration apiConfig, final Environment env) {
    final BeanConfig config = new BeanConfig();
    config.setTitle(apiConfig.getSwaggerConfiguration().getTitle());
    config.setDescription(apiConfig.getSwaggerConfiguration().getDescription());
    config.setResourcePackage(apiConfig.getSwaggerConfiguration().getResourcePackage());
    config.setScan(true);

    new AssetsBundle(apiConfig.getSwaggerConfiguration().getAssetsPath(),
        apiConfig.getSwaggerConfiguration().getAssetsPath(), null, "swagger").run(env);
    env.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    env.getObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    LOGGER.info("Registering ApiListingResource");
    env.jersey().register(new ApiListingResource());

    LOGGER.info("Registering SwaggerResource");
    final SwaggerResource swaggerResource =
        new SwaggerResource(apiConfig.getSwaggerConfiguration());
    env.jersey().register(swaggerResource);
  }

  private void registerHealthChecks(final DoraConfiguration config, final Environment env) {
    final HealthCheckRegistry health = env.healthChecks();
    health.register(HC_ES_CONFIG, new BasicDoraHealthCheck(config));
    health.register(HC_ES_STATUS, new ElasticsearchHealthCheck(config));
    health.register(HC_PHONETIC_PLUGIN,
        new ElasticsearchPluginHealthCheck(config, PHONETIC_PLUGIN));
    health.register(HC_X_PACK_PLUGIN, new ElasticsearchPluginHealthCheck(config, X_PACK_PLUGIN));
    registerIndexHealthChecks(config, env);

    if (!PROD_MODE.equals(config.getMode())) {
      registerRolesHealthChecks(config, env);
    }
  }

  private void registerRolesHealthChecks(DoraConfiguration config, Environment env) {
    final HealthCheckRegistry health = env.healthChecks();
    health.register(HC_WORKER_ROLE, new ElasticsearchRolesHealthCheck(config, WORKER_ROLE));
    health.register(HC_PEOPLE_WORKER_ROLE,
        new ElasticsearchRolesHealthCheck(config, PEOPLE_WORKER_ROLE));
    health.register(HC_PEOPLE_SENSITIVE_ROLE,
        new ElasticsearchRolesHealthCheck(config, PEOPLE_SENSITIVE_ROLE));
    health.register(HC_PEOPLE_SENSITIVE_NO_COUNTY_ROLE,
        new ElasticsearchRolesHealthCheck(config, PEOPLE_SENSITIVE_NO_COUNTY_ROLE));
    health.register(HC_PEOPLE_SEALED_ROLE,
        new ElasticsearchRolesHealthCheck(config, PEOPLE_SEALED_ROLE));
    health.register(HC_PEOPLE_SEALED_NO_COUNTY_ROLE,
        new ElasticsearchRolesHealthCheck(config, PEOPLE_SEALED_NO_COUNTY_ROLE));
    health.register(HC_PEOPLE_SUMMARY_WORKER_ROLE,
        new ElasticsearchRolesHealthCheck(config, PEOPLE_SUMMARY_WORKER_ROLE));
  }

  private void registerIndexHealthChecks(DoraConfiguration config, Environment env) {
    env.healthChecks().register(HC_PEOPLE_SUMMARY_INDEX,
        new ElasticsearchIndexHealthCheck(config, PEOPLE_SUMMARY_INDEX));
    env.healthChecks().register(HC_FACILITIES_INDEX,
        new ElasticsearchIndexHealthCheck(config, FACILITIES_INDEX));
  }

  private void runHealthChecks(Environment env) {
    for (Map.Entry<String, HealthCheck.Result> entry : env.healthChecks().runHealthChecks()
        .entrySet()) {
      if (!entry.getValue().isHealthy()) {
        LOGGER.error("Fail - {}: {}", entry.getKey(), entry.getValue().getMessage());
      }
    }
  }

  public void setGuiceBundle(GuiceBundle<DoraConfiguration> bundle) {
    this.guiceBundle = bundle;
  }

}

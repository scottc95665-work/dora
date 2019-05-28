package gov.ca.cwds.rest;

import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_AUDIT_EVENTS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_AUDIT_EVENTS_ALIAS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ES_CONFIG;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ES_STATUS;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_FACILITIES_CWS_ALIAS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_FACILITIES_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_FACILITIES_LIS_ALIAS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SEALED_NO_COUNTY_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SEALED_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SENSITIVE_NO_COUNTY_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SENSITIVE_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SUMMARY_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SUMMARY_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PHONETIC_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_USERS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_USERS_ALIAS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_WORKER_ROLE;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_X_PACK_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.Index.AUDIT_EVENTS_ES_ALIAS_ENDPOINT;
import static gov.ca.cwds.rest.DoraConstants.Index.AUDIT_EVENTS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Index.FACILITIES_CWS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Index.FACILITIES_ES_ALIAS_ENDPOINT;
import static gov.ca.cwds.rest.DoraConstants.Index.FACILITIES_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Index.FACILITIES_LIS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Index.PEOPLE_SUMMARY_INDEX;
import static gov.ca.cwds.rest.DoraConstants.Index.USERS_ES_ALIAS_ENDPOINT;
import static gov.ca.cwds.rest.DoraConstants.Index.USERS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.PROD_MODE;
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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Injector;
import com.google.inject.Module;

import gov.ca.cwds.dora.DoraUtils;
import gov.ca.cwds.dora.health.BasicDoraHealthCheck;
import gov.ca.cwds.dora.health.ElasticsearchHealthCheck;
import gov.ca.cwds.dora.health.ElasticsearchIndexHealthCheck;
import gov.ca.cwds.dora.health.ElasticsearchAliasWithIndexHealthCheck;
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

  @SuppressWarnings("unused")
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
  public final void runInternal(final DoraConfiguration configuration,
      final Environment environment) {
    EsRestClientManager esRestClientManager =
        new EsRestClientManager(configuration.getElasticsearchConfiguration());
    environment.lifecycle().manage(esRestClientManager);

    // register and run application health checks
    registerHealthChecks(configuration, environment);
    runHealthChecks(environment);

    Injector injector = guiceBundle.getInjector();

    environment.jersey().register(new ShiroExceptionMapper());
    environment.servlets().setSessionHandler(new SessionHandler());

    environment.servlets()
        .addFilter("AuditAndLoggingFilter",
            injector.getInstance(RequestResponseLoggingFilter.class))
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

    LOGGER.info("Application name: {}, Version: {}", configuration.getApplicationName(),
        DoraUtils.getAppVersion());

    LOGGER.info("Configuring CORS: Cross-Origin Resource Sharing");
    configureCors(environment);

    LOGGER.info("Configuring SWAGGER");
    configureSwagger(configuration, environment);
  }

  private static void configureCors(final Environment environment) {
    FilterRegistration.Dynamic filter =
        environment.servlets().addFilter("CORS", CrossOriginFilter.class);
    filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
    filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
    filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
    filter.setInitParameter("allowedHeaders",
        "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,X-Auth-Token");
    filter.setInitParameter("allowCredentials", "true");
  }

  private void configureSwagger(final DoraConfiguration apiConfiguration,
      final Environment environment) {
    BeanConfig config = new BeanConfig();
    config.setTitle(apiConfiguration.getSwaggerConfiguration().getTitle());
    config.setDescription(apiConfiguration.getSwaggerConfiguration().getDescription());
    config.setResourcePackage(apiConfiguration.getSwaggerConfiguration().getResourcePackage());
    config.setScan(true);

    new AssetsBundle(apiConfiguration.getSwaggerConfiguration().getAssetsPath(),
        apiConfiguration.getSwaggerConfiguration().getAssetsPath(), null, "swagger")
            .run(environment);
    environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    environment.getObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    LOGGER.info("Registering ApiListingResource");
    environment.jersey().register(new ApiListingResource());

    LOGGER.info("Registering SwaggerResource");
    final SwaggerResource swaggerResource =
        new SwaggerResource(apiConfiguration.getSwaggerConfiguration());
    environment.jersey().register(swaggerResource);
  }

  private void registerHealthChecks(final DoraConfiguration configuration,
      final Environment environment) {
    environment.healthChecks().register(HC_ES_CONFIG, new BasicDoraHealthCheck(configuration));
    environment.healthChecks().register(HC_ES_STATUS, new ElasticsearchHealthCheck(configuration));
    environment.healthChecks().register(HC_PHONETIC_PLUGIN,
        new ElasticsearchPluginHealthCheck(configuration, PHONETIC_PLUGIN));
    environment.healthChecks().register(HC_X_PACK_PLUGIN,
        new ElasticsearchPluginHealthCheck(configuration, X_PACK_PLUGIN));
    registerIndexHealthChecks(configuration, environment);
    registerAliasWithIndexHealthChecks(configuration, environment);
    if (!PROD_MODE.equals(configuration.getMode())) {
      registerRolesHealthChecks(configuration, environment);
    }
  }

  private void registerRolesHealthChecks(DoraConfiguration configuration, Environment environment) {
    environment.healthChecks().register(HC_WORKER_ROLE,
        new ElasticsearchRolesHealthCheck(configuration, WORKER_ROLE));
    environment.healthChecks().register(HC_PEOPLE_WORKER_ROLE,
        new ElasticsearchRolesHealthCheck(configuration, PEOPLE_WORKER_ROLE));
    environment.healthChecks().register(HC_PEOPLE_SENSITIVE_ROLE,
        new ElasticsearchRolesHealthCheck(configuration, PEOPLE_SENSITIVE_ROLE));
    environment.healthChecks().register(HC_PEOPLE_SENSITIVE_NO_COUNTY_ROLE,
        new ElasticsearchRolesHealthCheck(configuration, PEOPLE_SENSITIVE_NO_COUNTY_ROLE));
    environment.healthChecks().register(HC_PEOPLE_SEALED_ROLE,
        new ElasticsearchRolesHealthCheck(configuration, PEOPLE_SEALED_ROLE));
    environment.healthChecks().register(HC_PEOPLE_SEALED_NO_COUNTY_ROLE,
        new ElasticsearchRolesHealthCheck(configuration, PEOPLE_SEALED_NO_COUNTY_ROLE));
    environment.healthChecks().register(HC_PEOPLE_SUMMARY_WORKER_ROLE,
        new ElasticsearchRolesHealthCheck(configuration, PEOPLE_SUMMARY_WORKER_ROLE));
  }

  private void registerIndexHealthChecks(DoraConfiguration configuration, Environment environment) {
    environment.healthChecks().register(HC_PEOPLE_SUMMARY_INDEX,
        new ElasticsearchIndexHealthCheck(configuration, PEOPLE_SUMMARY_INDEX));
    environment.healthChecks().register(HC_FACILITIES_INDEX,
        new ElasticsearchIndexHealthCheck(configuration, FACILITIES_INDEX));
    environment.healthChecks().register(HC_AUDIT_EVENTS_INDEX,
        new ElasticsearchIndexHealthCheck(configuration, AUDIT_EVENTS_INDEX));
    environment.healthChecks().register(HC_USERS_INDEX,
        new ElasticsearchIndexHealthCheck(configuration, USERS_INDEX));
  }

  private void registerAliasWithIndexHealthChecks(DoraConfiguration configuration, Environment environment) {
    environment.healthChecks().register(HC_FACILITIES_LIS_ALIAS_INDEX,
        new ElasticsearchAliasWithIndexHealthCheck(configuration, FACILITIES_LIS_INDEX, FACILITIES_ES_ALIAS_ENDPOINT));
    environment.healthChecks().register(HC_FACILITIES_CWS_ALIAS_INDEX,
        new ElasticsearchAliasWithIndexHealthCheck(configuration, FACILITIES_CWS_INDEX, FACILITIES_ES_ALIAS_ENDPOINT));
    environment.healthChecks().register(HC_AUDIT_EVENTS_ALIAS_INDEX,
        new ElasticsearchAliasWithIndexHealthCheck(configuration, AUDIT_EVENTS_INDEX, AUDIT_EVENTS_ES_ALIAS_ENDPOINT));
    environment.healthChecks().register(HC_USERS_ALIAS_INDEX,
        new ElasticsearchAliasWithIndexHealthCheck(configuration, USERS_INDEX, USERS_ES_ALIAS_ENDPOINT));
  }

  private void runHealthChecks(Environment environment) {
    for (Map.Entry<String, HealthCheck.Result> entry : environment.healthChecks().runHealthChecks()
        .entrySet()) {
      if (!entry.getValue().isHealthy()) {
        LOGGER.error("Fail - {}: {}", entry.getKey(), entry.getValue().getMessage());
      }
    }
  }
}

package gov.ca.cwds.rest;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Injector;
import com.google.inject.Module;
import gov.ca.cwds.dora.DoraUtils;
import gov.ca.cwds.dora.health.*;
import gov.ca.cwds.inject.ApplicationModule;
import gov.ca.cwds.rest.filters.RequestResponseLoggingFilter;
import gov.ca.cwds.rest.resources.SwaggerResource;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.secnod.shiro.jaxrs.ShiroExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.Map;

/**
 * Core execution class of CWDS REST Dora server application.
 * <h3>Standard command line arguments:</h3>
 * <blockquote> server config/dora.yml </blockquote>
 * <h3>Standard JVM arguments:</h3>
 * <blockquote>-Djava.library.path=${workspace_loc:CWDS_API}/lib:/usr/local/lib/ </blockquote>
 *
 * @author CWDS API Team
 */
public final class DoraApplication extends BaseApiApplication<DoraConfiguration> {

  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(DoraApplication.class);

  private static final String PHONETIC_SEARCH_PLUGIN_NAME = "analysis-phonetic";
  private static final String X_PACK_PLUGIN_NAME = "x-pack";

  private static final String PEOPLE_INDEX = "people";
  private static final String FACILITIES_INDEX = "facilities";
  private static final String PEOPLE_SUMMARY_INDEX = "people_summary";
  private static final String SCREENING_INDEX = "screenings";

  private static final String WORKER_ROLE = "worker";
  private static final String PEOPLE_WORKER_ROLE = "people_worker";
  private static final String PEOPLE_SENSITIVE_ROLE = "people_sensitive";
  private static final String PEOPLE_SENSITIVE_NO_COUNTY_ROLE = "people_sensitive_no_county";
  private static final String PEOPLE_SEALED_ROLE = "people_sealed";
  private static final String PEOPLE_SEALED_NO_COUNTY_ROLE = "people_sealed_no_county";


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
  public final void runInternal(final DoraConfiguration configuration, final Environment environment) {
    //register and run application health checks
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
    environment.healthChecks().register("dora-es-config",
            new BasicDoraHealthCheck(configuration.getElasticsearchConfiguration()));
    environment.healthChecks().register("elasticsearch-status",
            new ElasticsearchHealthCheck(configuration.getElasticsearchConfiguration()));
    environment.healthChecks().register("elasticsearch-plugin-" + PHONETIC_SEARCH_PLUGIN_NAME,
            new ElasticsearchPluginHealthCheck(configuration.getElasticsearchConfiguration(), PHONETIC_SEARCH_PLUGIN_NAME));
    environment.healthChecks().register("elasticsearch-plugin-" + X_PACK_PLUGIN_NAME,
            new ElasticsearchPluginHealthCheck(configuration.getElasticsearchConfiguration(), X_PACK_PLUGIN_NAME));
    registerIndexHealthChecks(configuration, environment);
    registerRolesHealthChecks(configuration, environment);
  }

    private void registerRolesHealthChecks(DoraConfiguration configuration, Environment environment) {
        environment.healthChecks().register("elasticsearch-role-" + WORKER_ROLE,
                new ElasticsearchRolesHealthCheck(configuration.getElasticsearchConfiguration(), WORKER_ROLE));
        environment.healthChecks().register("elasticsearch-role-" + PEOPLE_WORKER_ROLE,
                new ElasticsearchRolesHealthCheck(configuration.getElasticsearchConfiguration(), PEOPLE_WORKER_ROLE));
        environment.healthChecks().register("elasticsearch-role-" + PEOPLE_SENSITIVE_ROLE,
                new ElasticsearchRolesHealthCheck(configuration.getElasticsearchConfiguration(), PEOPLE_SENSITIVE_ROLE));
        environment.healthChecks().register("elasticsearch-role-" + PEOPLE_SENSITIVE_NO_COUNTY_ROLE,
                new ElasticsearchRolesHealthCheck(configuration.getElasticsearchConfiguration(), PEOPLE_SENSITIVE_NO_COUNTY_ROLE));
        environment.healthChecks().register("elasticsearch-role-" + PEOPLE_SEALED_ROLE,
                new ElasticsearchRolesHealthCheck(configuration.getElasticsearchConfiguration(), PEOPLE_SEALED_ROLE));
        environment.healthChecks().register("elasticsearch-role-" + PEOPLE_SEALED_NO_COUNTY_ROLE,
                new ElasticsearchRolesHealthCheck(configuration.getElasticsearchConfiguration(), PEOPLE_SEALED_NO_COUNTY_ROLE));
    }

    private void registerIndexHealthChecks(DoraConfiguration configuration, Environment environment) {
        environment.healthChecks().register("elasticsearch-index-" + PEOPLE_INDEX,
            new ElasticsearchIndexHealthCheck(configuration.getElasticsearchConfiguration(), PEOPLE_INDEX));
        environment.healthChecks().register("elasticsearch-index-" + PEOPLE_SUMMARY_INDEX,
            new ElasticsearchIndexHealthCheck(configuration.getElasticsearchConfiguration(), PEOPLE_SUMMARY_INDEX));
        environment.healthChecks().register("elasticsearch-index-" + SCREENING_INDEX,
            new ElasticsearchIndexHealthCheck(configuration.getElasticsearchConfiguration(), SCREENING_INDEX));
        environment.healthChecks().register("elasticsearch-index-" + FACILITIES_INDEX,
            new ElasticsearchIndexHealthCheck(configuration.getElasticsearchConfiguration(), FACILITIES_INDEX));
    }

    private void runHealthChecks(Environment environment) {
    for (Map.Entry<String, HealthCheck.Result> entry :
            environment.healthChecks().runHealthChecks().entrySet()) {
      if (!entry.getValue().isHealthy()) {
        LOGGER.error("Fail - {}: {}", entry.getKey(), entry.getValue().getMessage());
      }
    }
  }
}

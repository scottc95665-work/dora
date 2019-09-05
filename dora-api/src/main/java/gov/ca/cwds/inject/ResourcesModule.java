package gov.ca.cwds.inject;

import java.io.IOException;

import javax.ws.rs.client.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import gov.ca.cwds.dora.DoraUtils;
import gov.ca.cwds.dora.security.FieldFilters;
import gov.ca.cwds.dora.tracelog.DoraTraceLogService;
import gov.ca.cwds.dora.tracelog.DoraTraceLogServiceAsync;
import gov.ca.cwds.rest.DoraConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.SwaggerConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.resources.IndexQueryResource;
import gov.ca.cwds.rest.resources.SwaggerResource;
import gov.ca.cwds.rest.resources.SystemInformationResource;
import gov.ca.cwds.rest.resources.TokenResource;

/**
 * Identifies all CWDS API domain resource classes available for dependency injection by Guice.
 * 
 * <p>
 * Note that Guice doesn't offer lazy loading of singleton objects, like Trace Log. Roll your own.
 * </p>
 *
 * @author CWDS API Team
 */
public class ResourcesModule extends AbstractModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesModule.class);

  private DoraTraceLogServiceAsync doraTraceLogServiceAsync;

  /**
   * Default, no-op constructor.
   */
  ResourcesModule() {
    // Default, no-op.
  }

  @Override
  protected void configure() {
    bind(SystemInformationResource.class);
    bind(SwaggerResource.class);
    bind(TokenResource.class);
    bind(IndexQueryResource.class);
  }

  @Provides
  public SwaggerConfiguration swaggerConfiguration(DoraConfiguration doraConfiguration) {
    return doraConfiguration.getSwaggerConfiguration();
  }

  @Provides
  public ElasticsearchConfiguration elasticsearchConfig(DoraConfiguration doraConfiguration) {
    return doraConfiguration.getElasticsearchConfiguration();
  }

  @Provides
  @Named("app.name")
  public String provideAppName(DoraConfiguration doraConfiguration) {
    return doraConfiguration.getApplicationName();
  }

  @Provides
  @Named("app.version")
  public String provideAppVersion() {
    return DoraUtils.getAppVersion();
  }

  @Provides
  @Inject
  public FieldFilters provideFieldFilters(ElasticsearchConfiguration esConfig) {
    FieldFilters fieldFilters = new FieldFilters();
    esConfig.getResponseFieldFilters().forEach((documentType, filePath) -> {
      try {
        fieldFilters.putFilter(documentType, filePath);
      } catch (IOException e) {
        String errorMessage =
            "Dora is not properly configured for filtering '" + documentType + "' documents";
        LOGGER.error(errorMessage);
        throw new DoraException(errorMessage, e);
      }
    });
    return fieldFilters;
  }

  protected synchronized void makeTraceLogService(DoraConfiguration config, Client client) {
    if (doraTraceLogServiceAsync == null) {
      doraTraceLogServiceAsync = new DoraTraceLogServiceAsync(config, client);
    }
  }

  @Provides
  @Inject
  // @Singleton // Guice can't deal
  public DoraTraceLogService provideTraceLog(DoraConfiguration config, Client client) {
    if (doraTraceLogServiceAsync == null) {
      makeTraceLogService(config, client);
    }
    return doraTraceLogServiceAsync;
  }

}

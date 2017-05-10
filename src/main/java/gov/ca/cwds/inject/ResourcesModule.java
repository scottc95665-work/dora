package gov.ca.cwds.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import gov.ca.cwds.rest.DoraConfiguration;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.SwaggerConfiguration;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.rest.resources.ApplicationResource;
import gov.ca.cwds.rest.resources.SimpleResourceDelegate;
import gov.ca.cwds.rest.resources.SwaggerResource;
import gov.ca.cwds.rest.services.es.IndexQueryService;

/**
 * Identifies all CWDS API domain resource classes available for dependency injection by Guice.
 *
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class ResourcesModule extends AbstractModule {

    /**
     * Default, no-op constructor.
     */
    ResourcesModule() {
        // Default, no-op.
    }

    @Override
    protected void configure() {
        bind(ApplicationResource.class);
        bind(SwaggerResource.class);
    }

    @Provides
    public SwaggerConfiguration swaggerConfiguration(DoraConfiguration doraConfiguration) {
        return doraConfiguration.getSwaggerConfiguration();
    }

    @Provides
    public ElasticsearchConfiguration elasticSearchConfig(DoraConfiguration doraConfiguration) {
        return doraConfiguration.getElasticsearchConfiguration();
    }

    @Provides
    @Named("app.name")
    public String appName(DoraConfiguration doraConfiguration) {
        return doraConfiguration.getApplicationName();
    }

    @Provides
    @Named("app.version")
    public String appVersion(DoraConfiguration doraConfiguration) {
        return doraConfiguration.getVersion();
    }

    @Provides
    @IndexQueryServiceResource
    public SimpleResourceDelegate<String, IndexQueryRequest, IndexQueryResponse, IndexQueryService> indexQueryResource(
            Injector injector) {
        return new SimpleResourceDelegate<>(injector.getInstance(IndexQueryService.class));
    }

}

package gov.ca.cwds.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import gov.ca.cwds.rest.ApiConfiguration;
import gov.ca.cwds.rest.SwaggerConfiguration;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.rest.resources.ApplicationResource;

// todo not needed lib dependency
//import gov.ca.cwds.rest.resources.ResourceDelegate;
//import gov.ca.cwds.rest.resources.ServiceBackedResourceDelegate;

import gov.ca.cwds.rest.resources.SimpleResourceDelegate;
import gov.ca.cwds.rest.resources.SwaggerResource;

// todo not needed lib dependency
//import gov.ca.cwds.rest.resources.TypedResourceDelegate;
//import gov.ca.cwds.rest.resources.TypedServiceBackedResourceDelegate;

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
    public SwaggerConfiguration swaggerConfiguration(ApiConfiguration apiConfiguration) {
        return apiConfiguration.getSwaggerConfiguration();
    }

    @Provides
    @Named("app.name")
    public String appName(ApiConfiguration apiConfiguration) {
        return apiConfiguration.getApplicationName();
    }

    @Provides
    @Named("app.version")
    public String appVersion(ApiConfiguration apiConfiguration) {
        return apiConfiguration.getVersion();
    }

    @Provides
    @IntakeIndexQueryServiceResource
    public SimpleResourceDelegate<String, IndexQueryRequest, IndexQueryResponse, IndexQueryService> intakeIndexQueryResource(
            Injector injector) {
        return new SimpleResourceDelegate<>(injector.getInstance(IndexQueryService.class));
    }

}

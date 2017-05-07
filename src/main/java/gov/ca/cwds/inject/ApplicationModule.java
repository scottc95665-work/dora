package gov.ca.cwds.inject;

import com.google.inject.AbstractModule;

import gov.ca.cwds.rest.DoraApplication;
import gov.ca.cwds.rest.ApiConfiguration;
import io.dropwizard.setup.Bootstrap;

/**
 * Bootstraps and configures the CWDS RESTful API application.
 *
 * @author CWDS API Team
 * @see DoraApplication
 */
public class ApplicationModule extends AbstractModule {

    private Bootstrap<ApiConfiguration> bootstrap;

    /**
     * Constructor. {@link AbstractModule#AbstractModule()}
     *
     * @param bootstrap API configuration
     */
    public ApplicationModule(Bootstrap<ApiConfiguration> bootstrap) {
        super();
        this.bootstrap = bootstrap;
    }

    /**
     * Configure and initialize API components, including services, resources, data access objects
     * (DAO), web service filters, and auditing.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        install(new ElasticsearchAccessModule());
        install(new ServicesModule());
        install(new ResourcesModule());
        install(new FiltersModule());
        install(new AuditingModule());
    }

}

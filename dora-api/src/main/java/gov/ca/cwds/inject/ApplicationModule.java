package gov.ca.cwds.inject;

import com.google.inject.AbstractModule;

import gov.ca.cwds.rest.DoraApplication;

/**
 * Bootstraps and configures the CWDS RESTful API application.
 *
 * @author CWDS API Team
 * @see DoraApplication
 */
public class ApplicationModule extends AbstractModule {

  /**
   * Constructor. {@link AbstractModule#AbstractModule()}
   */
  public ApplicationModule() {
    super();
  }

  /**
   * Configure and initialize API components, including services, resources, data access objects
   * (DAO), web service filters, and auditing. <p> {@inheritDoc}
   */
  @Override
  protected void configure() {
    install(new ServicesModule());
    install(new ResourcesModule());
    install(new AuditingModule());
    install(new FiltersModule());
  }
}

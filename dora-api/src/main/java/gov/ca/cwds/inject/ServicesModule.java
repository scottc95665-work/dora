package gov.ca.cwds.inject;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.client.Client;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import gov.ca.cwds.rest.services.es.IndexQueryService;

/**
 * Identifies all CWDS API business layer (aka, service) classes available for dependency injection
 * (aka, DI) by Google Guice.
 * 
 * @author CWDS API Team
 */
public class ServicesModule extends AbstractModule {

  /**
   * Default, no-op constructor.
   */
  ServicesModule() {
    // Default, no-op.
  }

  @Override
  protected void configure() {
    bind(IndexQueryService.class);

    // Trace Log:
    // Guice doesn't support lazy-loading singletons. :-(
    // bind(DoraTraceLogService.class).to(DoraTraceLogServiceAsync.class);
    // bind(DoraTraceLogService.class).to(DoraTraceLogServiceAsync.class).asEagerSingleton();
    // bind(DoraTraceLogServiceAsync.class).in(Singleton.class);
  }

  @Provides
  Validator provideValidator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Provides
  @Singleton
  public Client provideClient() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    final Client client =
        new JerseyClientBuilder().property(ClientProperties.CONNECT_TIMEOUT, 30000)
            .property(ClientProperties.READ_TIMEOUT, 30000)
            // Ignore host verification. Client will call trusted resources only.
            .hostnameVerifier((hostName, sslSession) -> true).build();
    client.register(new JacksonJsonProvider(mapper));
    return client;
  }

}

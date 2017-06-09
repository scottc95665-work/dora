package gov.ca.cwds.rest.resources;

import org.glassfish.hk2.api.ServiceLocator;
import org.junit.rules.ExternalResource;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.ServletModule;
import com.squarespace.jersey2.guice.JerseyGuiceModule;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;

/**
 * Code copied from {@link com.hubspot.dropwizard.guice.GuiceBundle} to create a guice injector for
 * jersey2 for unit testing.
 */
public class JerseyGuiceRule extends ExternalResource {

    @Override
    protected void before() throws Throwable {
        Injector baseInjector = Guice.createInjector(Stage.PRODUCTION, new ServletModule());
        JerseyGuiceUtils.install((name, parent) -> {
            if (!name.startsWith("__HK2_Generated_")) {
                return null;
            }

            return baseInjector.createChildInjector(new JerseyGuiceModule(name))
                    .getInstance(ServiceLocator.class);
        });
    }

    @Override
    protected void after() {
        JerseyGuiceUtils.reset();
    }

}

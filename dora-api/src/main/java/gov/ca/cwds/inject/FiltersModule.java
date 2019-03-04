package gov.ca.cwds.inject;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import com.google.inject.AbstractModule;
import gov.ca.cwds.rest.filters.RequestResponseLoggingFilter;

/**
 * Dependency injection (DI) for Filter classes.
 *
 * <p> Register filters her with Guice and configure them in {@link gov.ca.cwds.rest.DoraApplication},
 * method registerFilters. </p>
 *
 * @author CWDS API Team
 */
public class FiltersModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(RequestResponseLoggingFilter.class);
  }
  
  private File createFile(String file) {
    return new File(FilenameUtils.getFullPath(file), FilenameUtils.getName(file));
  }
}

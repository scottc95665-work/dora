package gov.ca.cwds.inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.inject.Guice;
import com.google.inject.Injector;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author TPT-2
 */
public class ServicesModuleTest {

  private ServicesModule module;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() throws Exception {
    module = new ServicesModule();
  }

  @Test
  public void testConfigure() throws Exception {
    Injector injector = Guice.createInjector(module);
    ServicesModule servicesModule = injector.getInstance(ServicesModule.class);
    assertNotNull(servicesModule);
  }

  @Test
  public void testValidatorShouldThrowIllegalArgumentException() throws Exception {
    thrown.expect(IllegalArgumentException.class);
    Set<ConstraintViolation<IndexQueryRequest>> violations = module.provideValidator()
        .validate(null);
  }

  @Test
  public void testValidator() throws Exception {
    Validator validator = module.provideValidator();
    IndexQueryRequest target = new IndexQueryRequest(null, null, null);
    Set<ConstraintViolation<IndexQueryRequest>> constraintViolations = validator
        .validateProperty(target, "query");
    assertEquals(0, constraintViolations.size());

    constraintViolations = validator.validateValue(IndexQueryRequest.class, "index", null);
    assertEquals(0, constraintViolations.size());
  }
}

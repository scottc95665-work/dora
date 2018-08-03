package gov.ca.cwds.inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.inject.Guice;
import com.google.inject.Injector;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest.IndexQueryRequestBuilder;
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

  private ServicesModule servicesModule;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() {
    servicesModule = new ServicesModule();
  }

  @Test
  public void testConfigure() {
    Injector injector = Guice.createInjector(servicesModule);
    ServicesModule servicesModule = injector.getInstance(ServicesModule.class);
    assertNotNull(servicesModule);
  }

  @Test
  public void testValidatorShouldThrowIllegalArgumentException() {
    thrown.expect(IllegalArgumentException.class);
    servicesModule.provideValidator().validate(null);
  }

  @Test
  public void testValidator() {
    Validator validator = servicesModule.provideValidator();
    IndexQueryRequest target = new IndexQueryRequestBuilder().addDocumentType(null)
        .addRequestBody(null).build();
    Set<ConstraintViolation<IndexQueryRequest>> constraintViolations = validator
        .validateProperty(target, "query");
    assertEquals(0, constraintViolations.size());

    constraintViolations = validator.validateValue(IndexQueryRequest.class, "index", null);
    assertEquals(0, constraintViolations.size());
  }
}

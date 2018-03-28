package gov.ca.cwds.rest.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.rest.SwaggerConfiguration;
import gov.ca.cwds.rest.views.SwaggerView;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;

/**
 * @author TPT-2
 */
public class SwaggerViewTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final String TEST_JSON_URL = "jsonurl";
  private static final String TEST_CALLBACK_URL = "callbackurl";

  private SwaggerView view;
  private static SwaggerConfiguration configuration;

  @BeforeClass
  public static void setUp() {
    configuration = new SwaggerConfiguration();
    Whitebox.setInternalState(configuration, "templateName", "testTemplateName");
    Whitebox.setInternalState(configuration, "title", "testTitle");
    Whitebox.setInternalState(configuration, "loginUrl", "testLoginUrl");
  }

  @Test
  public void testThatSwaggerViewIsCreatedSuccessfully() {
    view = new SwaggerView(configuration, TEST_JSON_URL, TEST_CALLBACK_URL);
    assertNotNull(view);
  }

  @Test
  public void testThatSwaggerPropertiesAreSetProperly() {
    view = new SwaggerView(configuration, TEST_JSON_URL, TEST_CALLBACK_URL);
    assertEquals(view.getJsonUrl(), TEST_JSON_URL);
    assertEquals(view.getCallbackUrl(), TEST_CALLBACK_URL);
  }

  @Test
  public void testThatSwaggerPropertiesShouldThrowNPE() {
    view = new SwaggerView(configuration, null, null);
    thrown.expect(NullPointerException.class);
    String jsonUrl = view.getJsonUrl();
    int jsonUrlLength = jsonUrl.length();
    String callbackUrl = view.getCallbackUrl();
    int callbackUrlLength = callbackUrl.length();
  }

  @Test
  public void testSwaggerProperties() {
    view = new SwaggerView(configuration, null, null);
    assertNotNull(view.getTitle());
    assertNotNull(view.getLoginUrl());
    assertTrue(view.getShowLoginButton());
    assertNull(view.getLogo());
    assertNull(view.getSpId());
  }
}

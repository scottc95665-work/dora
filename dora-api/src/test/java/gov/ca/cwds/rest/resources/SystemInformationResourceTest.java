package gov.ca.cwds.rest.resources;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import com.codahale.metrics.health.HealthCheck.Result;
import com.codahale.metrics.health.HealthCheckRegistry;
import gov.ca.cwds.dto.app.SystemInformationDto;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.ResourceTestRule;
/**
 * @author CWDS TPT-2
 */
public class SystemInformationResourceTest {
  private static final String APP_NAME = "CWDS Dora";
  private static final String APP_VERSION = "1.0";
  private static final String SYS_INFO_PATH = "/system-information";
  private static final Environment environment = mock(Environment.class);
  private static final HealthCheckRegistry healthCheckRegistry = mock(HealthCheckRegistry.class);
  static {
    when(environment.healthChecks()).thenReturn(healthCheckRegistry);
  }
  @ClassRule
  public final static ResourceTestRule systemInfoResouce = ResourceTestRule.builder()
      .addResource(new SystemInformationResource(APP_NAME, APP_VERSION, environment)).build();
  
  @Before
  public void setUp() {}
  
  @Test
  public void testGetHealthyResponse() throws Exception {
    final Map<String, Result> healthCheckResults = new TreeMap<>();
    healthCheckResults.put("test1_health", Result.healthy("test1 is healthy"));
    healthCheckResults.put("test2_health", Result.healthy("test2 is not healthy"));
    doReturn(healthCheckResults).when(healthCheckRegistry).runHealthChecks();
    testResource(SYS_INFO_PATH, builder -> builder.get(), "1");
  }
  
  private void testResource(String path, Function<Builder, Response> restOperation,
      String assertResponseBody) throws IOException, JSONException {
    Response actualResponse =
        restOperation.apply(systemInfoResouce.target(path).request(MediaType.APPLICATION_JSON));
    String actualResponseBody = IOUtils.toString((ByteArrayInputStream) actualResponse.getEntity(),
        StandardCharsets.UTF_8.displayName());
    JSONObject responseObj = new JSONObject(actualResponseBody);
    JSONObject test1Helath = new JSONObject(responseObj.getJSONObject("health_checks").getString("test1_health"));
    assertEquals("test1 is healthy", test1Helath.getString("message"));
    assertEquals("true", test1Helath.getString("healthy"));
  }
  
  @Test
  public void testGetSystemInformationDto() throws Exception {
    Map<String, Result> healthCheckResults = new TreeMap<>();
    healthCheckResults.put("test1_health", Result.healthy("test1 is healthy"));
    doReturn(healthCheckResults).when(healthCheckRegistry).runHealthChecks();
    SystemInformationDto sysInfoDto =
        systemInfoResouce.target(SYS_INFO_PATH).request().get(SystemInformationDto.class);
    assertEquals(APP_NAME, sysInfoDto.getApplicationName());
    assertEquals(APP_VERSION, sysInfoDto.getVersion());
    assertTrue(sysInfoDto.isHealthStatus());
    }

}
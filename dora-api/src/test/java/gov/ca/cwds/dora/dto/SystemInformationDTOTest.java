package gov.ca.cwds.dora.dto;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.junit.ExpectedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * @author CWDS TPT-2
 */
public class SystemInformationDTOTest {

  private static final String APP_NAME = "testAppName";
  private static final String APP_NAME_2 = "anotherTestAppName";
  private static final String VERSION = "testVersion";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Spy
  private SystemInformationDTO systemInformationDTO; // "Class Under Test"

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void type() throws Exception {
    assertThat(SystemInformationDTO.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(systemInformationDTO, notNullValue());
  }

  @Test
  public void testToString() throws Exception {
    systemInformationDTO.setApplicationName(APP_NAME);
    systemInformationDTO.setVersion(VERSION);

    Assert.assertTrue(systemInformationDTO.toString().contains(APP_NAME));
    Assert.assertTrue(systemInformationDTO.toString().contains(VERSION));
  }

  @Test
  public void testEqualsAndHashCode() throws Exception {
    systemInformationDTO.setApplicationName(APP_NAME);
    SystemInformationDTO anotherSystemInformationDTO = new SystemInformationDTO();
    anotherSystemInformationDTO.setApplicationName(APP_NAME_2);
    Assert.assertFalse(systemInformationDTO.equals(anotherSystemInformationDTO));
    Assert.assertTrue(systemInformationDTO.hashCode() != anotherSystemInformationDTO.hashCode());
  }
}

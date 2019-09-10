package gov.ca.cwds.auth;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.hamcrest.junit.ExpectedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * @author CWDS TPT-2
 */
public class PerryUserIdentityTest {

  private static final String STAFF_ID = "TEST_STAFF_ID";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Spy
  private PerryUserIdentity perryUserIdentity; // "Class Under Test"

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void type() throws Exception {
    assertThat(PerryUserIdentity.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(perryUserIdentity, notNullValue());
  }

  @Test
  public void testStaffId() throws Exception {
    assertNull(perryUserIdentity.getStaffId());
    perryUserIdentity.setStaffId(STAFF_ID);
    assertNotNull(perryUserIdentity.getStaffId());
    assertEquals(STAFF_ID, perryUserIdentity.getStaffId());
  }

}

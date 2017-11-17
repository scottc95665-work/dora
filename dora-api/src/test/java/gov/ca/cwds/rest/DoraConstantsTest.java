package gov.ca.cwds.rest;

import static gov.ca.cwds.rest.DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY;
import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author CWDS TPT-2
 */
public class DoraConstantsTest {

  @Test
  public void type() throws Exception {
    assertThat(DoraConstants.class, notNullValue());
  }

  @Test
  public void testConstants() throws Exception {
    Assert.assertTrue("system-information".equals(SYSTEM_INFORMATION));
    Assert.assertTrue("dora".equals(RESOURCE_ELASTICSEARCH_INDEX_QUERY));
  }
}

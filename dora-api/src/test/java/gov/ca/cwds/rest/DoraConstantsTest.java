package gov.ca.cwds.rest;

import static gov.ca.cwds.rest.DoraConstants.DEV_MODE;
import static gov.ca.cwds.rest.DoraConstants.PROD_MODE;
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
  public void type() {
    assertThat(DoraConstants.class, notNullValue());
  }

  @Test
  public void testConstants() {
    Assert.assertTrue("system-information".equals(SYSTEM_INFORMATION));
    Assert.assertTrue("dora".equals(RESOURCE_ELASTICSEARCH_INDEX_QUERY));
    Assert.assertEquals("PROD", PROD_MODE);
    Assert.assertEquals("DEV", DEV_MODE);
  }

}

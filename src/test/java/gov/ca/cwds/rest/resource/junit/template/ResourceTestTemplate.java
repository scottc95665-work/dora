package gov.ca.cwds.rest.resource.junit.template;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

/**
 * 
 * @author CWDS API Team
 *
 */
public interface ResourceTestTemplate {

  static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  /*
   * JUnit test for GET
   */
  @SuppressWarnings("javadoc")
  @Test
  public void testGetDelegatesToResourceDelegate() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testGet201ResourceSuccess() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testGet404NotFoundError() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testGet501NotImplemented() throws Exception;

  /*
   * JUnit test for POST
   */
  @SuppressWarnings("javadoc")
  @Test
  public void testPostDelegatesToResourceDelegate() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testPostValidatesEntity() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testPost400JSONError() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testPost406NotSupportedError() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testPost409AlreadyExistsError() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testPost422ValidationError() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testPost200ResourceSuccess() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testPost501NotImplemented() throws Exception;

  /*
   * JUnit test for DELETE
   */

  @SuppressWarnings("javadoc")
  @Test
  public void testDeleteDelegatesToResource() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testDelete200ResourceSuccess() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testDelete404NotFoundError() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testDelete501NotImplemented() throws Exception;

  /*
   * JUnit for PUT
   */
  @SuppressWarnings("javadoc")
  @Test
  public void testUpdateDelegatesToResourceDelegate() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testUpdate200ResourceSuccess() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testUpdate400JSONError() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testUpdate404NotFoundError() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testUpdate406NotSupportedError() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testUpdate422ValidationError() throws Exception;

  @SuppressWarnings("javadoc")
  @Test
  public void testUpdate501NotImplemented() throws Exception;

}

package gov.ca.cwds.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author CWDS API Team
 */
public class SwaggerConfigurationTest {

  @Test
  public void type() throws Exception {
    assertThat(SwaggerConfiguration.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    assertThat(target, notNullValue());
  }

  @Test
  public void getTemplateName_Args__() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    String actual = target.getTemplateName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAssetsPath_Args__() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    String actual = target.getAssetsPath();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getResourcePackage_Args__() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    String actual = target.getResourcePackage();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getTitle_Args__() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    String actual = target.getTitle();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDescription_Args__() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    String actual = target.getDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJsonUrl_Args__() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    String actual = target.getJsonUrl();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLogo_Args__() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    String actual = target.getLogo();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLoginUrl_Args__() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    String actual = target.getLoginUrl();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isShowSwagger_Args__() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    boolean actual = target.isShowSwagger();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSpId_Args__() throws Exception {
    SwaggerConfiguration target = new SwaggerConfiguration();
    String actual = target.getSpId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}

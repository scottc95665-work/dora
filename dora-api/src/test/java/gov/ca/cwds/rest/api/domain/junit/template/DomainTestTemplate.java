package gov.ca.cwds.rest.api.domain.junit.template;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * 
 * Domain JUnit test template
 * 
 * @author CWDS API Team
 *
 */
public interface DomainTestTemplate {

  @SuppressWarnings("javadoc")
  @ClassRule


  @Before
  public abstract void setup() throws Exception;

  @SuppressWarnings("javadoc")
  @After
  public abstract void teardown() throws Exception;

  /*
   * class level JUnits
   */
  @SuppressWarnings("javadoc")
  @Test
  public void testEqualsHashCodeWorks() throws Exception;


  @SuppressWarnings("javadoc")
  @Test
  public abstract void testSerializesToJSON() throws Exception;


  @SuppressWarnings("javadoc")
  @Test
  public abstract void testDeserializesFromJSON() throws Exception;


  @SuppressWarnings("javadoc")
  @Test
  public abstract void testPersistentConstructor() throws Exception;


  @SuppressWarnings("javadoc")
  @Test
  public abstract void testJSONCreatorConstructor() throws Exception;


  @SuppressWarnings("javadoc")
  @Test
  public abstract void testSuccessWithValid() throws Exception;


  @SuppressWarnings("javadoc")
  @Test
  public abstract void testSuccessWithOptionalsNotIncluded() throws Exception;

  /*
   * Property JUnit Test
   * 
   * for each property of the bean, create appropriate tests depending upon the annotations on the
   * property
   * 
   */

  /*
   * Case: Boolean NotNull NotRequired
   * 
   * @NotNull
   * 
   * @ApiModelProperty(required = false, readOnly = false) private Boolean property;
   */

  /*
   * Case: Boolean NotNull Required
   * 
   * @NotNull
   * 
   * @ApiModelProperty(required = true) private Boolean property
   */

  /*
   * Case: String NotNull NotRequired Size(min=x, max=x)
   * 
   * @NotNull
   * 
   * @Size(min = 10, max = 10)
   * 
   * @ApiModelProperty(required = false) private String Property;
   * 
   */

  /*
   * Case: String NotEmpty Required Size(min=x, max=y)
   * 
   * @NotEmpty
   * 
   * @Size(min = 1, max = 2)
   * 
   * @ApiModelProperty(required = true, readOnly = false) private String property;
   */

  /*
   * Case: String NotRequired Size(max=x)
   * 
   * @Size(max = 10)
   * 
   * @ApiModelProperty(required = false, readOnly = false) private String property;
   */

  /*
   * Case: String NotRequired DateNotRequired
   * 
   * @gov.ca.cwds.rest.validation.Date(format = DATE_FORMAT, required = false)
   * 
   * @ApiModelProperty(required = false, readOnly = false) private String property
   */

  /*
   * Case: String NotNull Required DateRequired
   * 
   * @NotNull
   * 
   * @gov.ca.cwds.rest.validation.Date(format = DATE_FORMAT, required = true)
   * 
   * @ApiModelProperty(required = true, readOnly = fals) private String property;
   */

  /*
   * Case: String NotEmpty Size(min=x, max=x) OneOf Required
   * 
   * @NotEmpty
   * 
   * @Size(min = 1, max = 1)
   * 
   * @OneOf(value = {"S", "R", "N"}, ignoreCase = true, ignoreWhitespace = true)
   * 
   * @ApiModelProperty(required = true) private String property;
   */

  /*
   * Case: Short NotNull NotRequired
   * 
   * @NotNull
   * 
   * @ApiModelProperty(required = false, readOnly = false) private Short property;
   */

  /*
   * Case: Short NotRequired
   * 
   * @ApiModelProperty(required = false, readOnly = false) private Short property;
   */

  /*
   * Case: BigDecimal NotNull Required
   * 
   * @NotNull
   * 
   * @ApiModelProperty(required = true, readOnly = false) private BigDecimal property;
   */

  /*
   * Case: Integer NotNullRequired
   * 
   * @NotNull
   * 
   * @ApiModelProperty(required = true, readOnly = false) private Integer phoneExt;
   */
}

package gov.ca.cwds.xpack.realm.perry;

import static gov.ca.cwds.xpack.realm.utils.Constants.CALS_ADMIN;
import static gov.ca.cwds.xpack.realm.utils.Constants.COUNTY_ADMIN;
import static gov.ca.cwds.xpack.realm.utils.Constants.OFFICE_ADMIN;
import static gov.ca.cwds.xpack.realm.utils.Constants.STATE_ADMIN;
import static gov.ca.cwds.xpack.realm.utils.PerryRealmUtils.parsePerryTokenFromJSON;
import static java.net.HttpURLConnection.HTTP_OK;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.ca.cwds.xpack.realm.CwdsPrivileges;
import gov.ca.cwds.xpack.realm.utils.JsonTokenInfoHolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.xpack.core.security.authc.AuthenticationResult;
import org.elasticsearch.xpack.core.security.authc.AuthenticationToken;
import org.elasticsearch.xpack.core.security.authc.Realm;
import org.elasticsearch.xpack.core.security.authc.RealmConfig;
import org.elasticsearch.xpack.core.security.user.User;

/**
 * A custom {@link Realm} implementation that expects a valid Perry Token in the "Authorization"
 * HTTP header. The Perry Token is decoded into Json Token and its properties are used to build a
 * list of X-Pack roles.
 *
 * @author CWDS TPT-2
 */
public class PerryRealm extends Realm {

  static final String REALM_TYPE = "custom";

  private static final String AUTHORIZATION_HEADER = "Authorization";

  private static final String SETTING_VALIDATION = "token_validation_url";

  private static final String WORKER = "worker";
  private static final String PEOPLE_WORKER = "people_worker";
  private static final String PEOPLE_SENSITIVE = "people_sensitive";
  private static final String PEOPLE_SENSITIVE_NO_COUNTY = "people_sensitive_no_county";
  private static final String PEOPLE_SEALED = "people_sealed";
  private static final String PEOPLE_SEALED_NO_COUNTY = "people_sealed_no_county";
  private static final String FACILITIES_READ = "facilities_read";
  private static final String FACILITIES_READ_ADOPTIONS = "facilities_read_adoptions";

  private static final String PEOPLE_SUMMARY_WORKER = "people_summary_worker";
  private static final String ADDING_ROLE = "adding {} role";

  private String tokenValidationUrl;

  /**
   * Constructor for the Realm. This constructor delegates to the super class to initialize the
   * common aspects such as the logger.
   *
   * @param config the configuration specific to this realm
   */
  PerryRealm(RealmConfig config) {
    super(REALM_TYPE, config);
    tokenValidationUrl = config.settings().get(SETTING_VALIDATION);
  }

  /**
   * Indicates whether this realm supports the given token. This realm only support {@link
   * PerryToken} objects for authentication
   *
   * @param authenticationToken the token to test for support
   * @return true if the token is supported. false otherwise
   */
  @Override
  public boolean supports(AuthenticationToken authenticationToken) {
    return authenticationToken instanceof PerryToken;
  }

  /**
   * This method will extract a token from the given {@link RestRequest} if possible.
   *
   * @param threadContext the {@link ThreadContext} that contains headers and transient objects for
   * a request
   * @return the {@link AuthenticationToken} if possible to extract or <code>null</code>
   */
  @Override
  public AuthenticationToken token(ThreadContext threadContext) {
    String token = threadContext.getHeader(AUTHORIZATION_HEADER);
    return token == null ? null : new PerryToken(token);
  }

  @Override
  public void lookupUser(String s, ActionListener<User> actionListener) {
    throw new UnsupportedOperationException();
  }

  private String validatePerryToken(String token)
      throws IOException, PerryTokenValidationException {
    long timeBeforeTokenValidation = System.currentTimeMillis();
    try (CloseableHttpClient httpClient = HttpClients.createMinimal()) {
      HttpGet httpGet = new HttpGet(tokenValidationUrl + token);
      try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
        logger.debug("PerryRealm: Token Validation took {} milliseconds",
            System.currentTimeMillis() - timeBeforeTokenValidation);
        if (HTTP_OK == response.getStatusLine().getStatusCode()) {
          return EntityUtils.toString(response.getEntity(), "UTF-8");
        } else {
          throw new PerryTokenValidationException("invalid Perry token");
        }
      }
    }
  }

  /**
   * Method that handles the actual authentication of the token. This method will only be called if
   * the token is a supported token. The method validates the Perry Token. If the Perry Token is
   * valid, a {@link User} will be returned as the argument to the {@code listener}'s {@link
   * ActionListener#onResponse(Object)} method. Else {@code null} is returned.
   *
   * @param authenticationToken the token to authenticate
   * @param actionListener return authentication result by calling {@link ActionListener#onResponse(Object)}
   */
  @Override
  public void authenticate(AuthenticationToken authenticationToken, ActionListener<AuthenticationResult> actionListener) {
    long timeBeforeAuthenticate = System.currentTimeMillis();

    try {
      String jsonToken = validatePerryToken(authenticationToken.principal());
      JsonTokenInfoHolder jsonTokenInfoHolder = parsePerryTokenFromJSON(jsonToken);
      CwdsPrivileges cwdsPrivileges = CwdsPrivileges.buildPrivileges(jsonTokenInfoHolder);
      logger.info(cwdsPrivileges);

      ArrayList<String> rolesList = new ArrayList<>();
      addRole(rolesList, WORKER);

      if (jsonTokenInfoHolder.getRoles().contains(CALS_ADMIN)) {
        addRole(rolesList, CALS_ADMIN);
      } else if (jsonTokenInfoHolder.getRoles().contains(STATE_ADMIN)) {
        addRole(rolesList, STATE_ADMIN);
      } else if (jsonTokenInfoHolder.getRoles().contains(COUNTY_ADMIN)) {
        addRole(rolesList, COUNTY_ADMIN);
      } else if (jsonTokenInfoHolder.getRoles().contains(OFFICE_ADMIN)) {
        addRole(rolesList, OFFICE_ADMIN);
      }

      if (cwdsPrivileges.isSocialWorkerOnly()) {
        setSocialWorkerOnlyRoles(rolesList);
      }

      if (cwdsPrivileges.isCountySensitive() || cwdsPrivileges.isStateSensitive()) {
        setSocialWorkerOnlyRoles(rolesList);
        addRole(rolesList, PEOPLE_SENSITIVE);
      }

      if (cwdsPrivileges.isCountySensitive()) {
        addRole(rolesList, PEOPLE_SENSITIVE_NO_COUNTY);
      }

      if (cwdsPrivileges.isCountySealed() || cwdsPrivileges.isStateSealed()) {
        setSocialWorkerOnlyRoles(rolesList);
        addRole(rolesList, PEOPLE_SEALED);
        addRole(rolesList, PEOPLE_SEALED_NO_COUNTY);
      }

      if (cwdsPrivileges.isFacilitiesReadAdoptions()){
        addRole(rolesList, FACILITIES_READ_ADOPTIONS);
      } else if (cwdsPrivileges.isFacilitiesRead()) {
        addRole(rolesList, FACILITIES_READ);
      }

      String[] roles = rolesList.toArray(new String[rolesList.size()]);
      logger.info("roles: " + rolesList);

      Map<String, Object> metadata = new HashMap<>();
      metadata.put("county_id", cwdsPrivileges.getCountyId());
      metadata.put("county_name", cwdsPrivileges.getCountyName());

      User user = new User("perry", roles, "full name", "email@a.net", metadata, true);
      actionListener.onResponse(AuthenticationResult.success(user));

      logger.debug("PerryRealm: authenticate took {} milliseconds",
          System.currentTimeMillis() - timeBeforeAuthenticate);

    } catch (PerryTokenValidationException e) {
      logger.warn("invalid Perry Token: " + e.getMessage(), e);
      actionListener.onResponse(null);
    } catch (JsonProcessingException | IllegalArgumentException e) {
      logger.warn("failed to parse Json Token: " + e.getMessage(), e);
      actionListener.onResponse(null);
    } catch (IOException e) {
      actionListener.onFailure(e);
    }
  }

  private void addRole(ArrayList<String> rolesList, String role) {
    rolesList.add(role);
    logger.debug(ADDING_ROLE, role);
  }

  private void setSocialWorkerOnlyRoles(ArrayList<String> rolesList) {
    addRole(rolesList, PEOPLE_WORKER);
    addRole(rolesList, PEOPLE_SUMMARY_WORKER);
  }
}

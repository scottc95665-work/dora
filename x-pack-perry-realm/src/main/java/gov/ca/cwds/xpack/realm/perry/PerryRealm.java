package gov.ca.cwds.xpack.realm.perry;

import static java.net.HttpURLConnection.HTTP_OK;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.ca.cwds.xpack.realm.CwdsPrivileges;
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
import org.elasticsearch.xpack.security.authc.AuthenticationToken;
import org.elasticsearch.xpack.security.authc.Realm;
import org.elasticsearch.xpack.security.authc.RealmConfig;
import org.elasticsearch.xpack.security.user.User;

/**
 * A custom {@link Realm} implementation that expects a valid Perry Token in the "Authorization" HTTP header.
 * The Perry Token is decoded into Json Token and its properties are used to build a list of X-Pack roles.
 *
 * @author CWDS TPT-2
 */
public class PerryRealm extends Realm {

  static final String REALM_TYPE = "custom";

  private static final String AUTHORIZATION_HEADER = "Authorization";

  private static final String SETTING_VALIDATION = "token_validation_url";

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
   * Indicates whether this realm supports the given token. This realm only support {@link PerryToken} objects for authentication
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
   * @param threadContext the {@link ThreadContext} that contains headers and transient objects for a request
   * @return the {@link AuthenticationToken} if possible to extract or <code>null</code>
   */
  @Override
  public AuthenticationToken token(ThreadContext threadContext) {
    String token = threadContext.getHeader(AUTHORIZATION_HEADER);
    return token == null ? null : new PerryToken(token);
  }

  private String validatePerryToken(String token) throws IOException, PerryTokenValidationException {
    try (CloseableHttpClient httpClient = HttpClients.createMinimal()) {
      HttpGet httpGet = new HttpGet(tokenValidationUrl + token);
      try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
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
   * the token is a supported token. The method validates the Perry Token.
   * If the Perry Token is valid, a {@link User} will be returned as the argument to the {@code listener}'s
   * {@link ActionListener#onResponse(Object)} method. Else {@code null} is returned.
   *
   * @param authenticationToken the token to authenticate
   * @param listener return authentication result by calling {@link ActionListener#onResponse(Object)}
   */
  @Override
  public void authenticate(AuthenticationToken authenticationToken, ActionListener<User> listener) {
    try {
      CwdsPrivileges cwdsPrivileges = CwdsPrivileges
          .fromJson(validatePerryToken(authenticationToken.principal()));
      logger.info(cwdsPrivileges);

      ArrayList<String> rolesList = new ArrayList<>();
      rolesList.add("worker");
      rolesList.add("people_worker");

      if (cwdsPrivileges.isCountySensitive() || cwdsPrivileges.isStateSensitive()) {
        rolesList.add("people_sensitive");
      }

      if (cwdsPrivileges.isCountySealed() || cwdsPrivileges.isStateSealed()) {
        rolesList.add("people_sealed");
      }

      String[] roles = rolesList.toArray(new String[rolesList.size()]);
      logger.info("roles: " + rolesList);

      Map<String, Object> metadata = new HashMap<>();
      metadata.put("county_id", cwdsPrivileges.getCountyId());

      User user = new User("perry", roles, "full name", "email@a.net", metadata, true);
      listener.onResponse(user);

    } catch (PerryTokenValidationException e) {
      logger.warn("invalid Perry Token: " + e.getMessage());
      listener.onResponse(null);
    } catch (JsonProcessingException e) {
      logger.warn("failed to parse Json Token: " + e.getMessage());
      listener.onResponse(null);
    } catch (IOException e) {
      listener.onFailure(e);
    }
  }

  /** @deprecated */
  @Deprecated
  @Override
  public User authenticate(AuthenticationToken authenticationToken) {
    throw new UnsupportedOperationException("Deprecated");
  }

  /** @deprecated */
  @Deprecated
  @Override
  public User lookupUser(String s) {
    throw new UnsupportedOperationException();
  }

  /** @deprecated */
  @Deprecated
  @Override
  public boolean userLookupSupported() {
    return false;
  }
}
package gov.ca.cwds.xpack.realm.perry;

import org.elasticsearch.xpack.security.authc.Realm;
import org.elasticsearch.xpack.security.authc.RealmConfig;

/**
 * The factory class for the {@link PerryRealm}. This factory class is responsible for properly constructing the realm
 * when called by the X-Pack framework.
 *
 * @author CWDS TPT-2
 */
final class PerryRealmFactory implements Realm.Factory {
  /**
   * Create a {@link PerryRealm} based on the given configuration
   * @param config the configuration to create the realm with
   * @return the realm
   */
  @Override
  public PerryRealm create(RealmConfig config) {
    return new PerryRealm(config);
  }
}

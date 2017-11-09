package gov.ca.cwds.xpack.realm.perry;

import java.util.Map;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.watcher.ResourceWatcherService;
import org.elasticsearch.xpack.extensions.XPackExtension;
import org.elasticsearch.xpack.security.authc.Realm.Factory;

/**
 * The extension class that serves as the integration point between Elasticsearch, X-Pack,
 * and the custom authentication realm that is provided by this extension.
 *
 * @author CWDS TPT-2
 */
public class PerryRealmExtension extends XPackExtension {

  @Override
  public String name() {
    return "Perry Realm";
  }

  @Override
  public String description() {
    return "Perry Realm Extension for X-Pack";
  }

  /**
   * Returns a map of the realms provided by this extension. The first parameter is the string representation of the realm type;
   * this is the value that is specified when declaring a realm in the settings. Note, the realm type cannot be one of the types
   * defined by X-Pack. In order to avoid a conflict, you may wish to use some prefix to your realm types.
   *
   * The second parameter is an instance of the {@link Factory} implementation. This factory class will be used to create realms of
   * this type that are defined in the elasticsearch settings.
   */
  @Override
  public Map<String, Factory> getRealms(ResourceWatcherService resourceWatcherService) {
    return new MapBuilder<String, Factory>()
        .put(PerryRealm.REALM_TYPE, new PerryRealmFactory())
        .immutableMap();
  }
}

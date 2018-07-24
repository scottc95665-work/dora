package gov.ca.cwds.dora.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CWDS TPT-2
 */
public class FieldFilters {
  private Map<String, FieldFilterScript> filters;

  public FieldFilters() {
    filters = new HashMap<>();
  }

  public void putFilter(String documentType, String filePath) throws IOException {
    filters.put(documentType, new FieldFilterScript(filePath));
  }

  public FieldFilterScript getFilter(String documentType) {
    return filters.get(documentType);
  }

  public boolean hasFilter(String documentType) {
    return filters.containsKey(documentType);
  }
}

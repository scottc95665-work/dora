package gov.ca.cwds.dora.tracelog;

public interface DoraTraceLogService {

  /**
   * Log search query terms.
   * 
   * @param userId user conducting the search
   * @param index index to search
   * @param json search query
   */
  void logSearchQuery(String userId, String index, String json);

}

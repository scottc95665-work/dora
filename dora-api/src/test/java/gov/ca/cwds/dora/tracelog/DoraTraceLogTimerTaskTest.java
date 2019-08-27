package gov.ca.cwds.dora.tracelog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import gov.ca.cwds.rest.DoraConfiguration;

public class DoraTraceLogTimerTaskTest {

  final Client client = Mockito.mock(Client.class);
  final DoraConfiguration config = Mockito.mock(DoraConfiguration.class);
  final Queue<DoraTraceLogSearchEntry> searchQueue = new ConcurrentLinkedQueue<>();
  DoraTraceLogTimerTask target;

  @Before
  public void setup() throws Exception {
    when(config.getTraceLogUrl())
        .thenReturn("https://ferbapi.integration.cwds.io/trace_log_search/MORGOTH/people-summary");
    target = new DoraTraceLogTimerTask(config, client, searchQueue);
  }

  @Test
  public void sendSearchQueryStatusGood() throws Exception {
    final String json =
        "{\"size\":\"250\",\"track_scores\":\"true\",\"sort\":[{\"_score\":\"desc\",\"last_name.keyword\":\"asc\",\"first_name.keyword\":\"asc\",\"_uid\":\"desc\"}],\"min_score\":\"2.5\",\"_source\":[\"id\",\"legacy_source_table\",\"first_name\",\"middle_name\",\"last_name\",\"name_suffix\",\"gender\",\"akas\",\"date_of_birth\",\"date_of_death\",\"ssn\",\"languages\",\"races\",\"ethnicity\",\"client_counties\",\"case_status\",\"addresses.id\",\"addresses.effective_start_date\",\"addresses.street_name\",\"addresses.street_number\",\"addresses.city\",\"addresses.county\",\"addresses.state_code\",\"addresses.zip\",\"addresses.type\",\"addresses.legacy_descriptor\",\"addresses.phone_numbers.number\",\"addresses.phone_numbers.type\",\"csec.start_date\",\"csec.end_date\",\"csec.csec_code_id\",\"csec.description\",\"sp_county\",\"sp_phone\",\"legacy_descriptor\",\"highlight\",\"phone_numbers.id\",\"phone_numbers.number\",\"phone_numbers.type\",\"estimated_dob_code\",\"sensitivity_indicator\",\"race_ethnicity\",\"open_case_responsible_agency_code\"],\"highlight\":{\"order\":\"score\",\"number_of_fragments\":\"10\",\"require_field_match\":\"false\",\"fields\":{\"autocomplete_search_bar\":{\"matched_fields\":[\"autocomplete_search_bar\",\"autocomplete_search_bar.phonetic\",\"autocomplete_search_bar.diminutive\"]},\"searchable_date_of_birth\":{}}},\"query\":{\"function_score\":{\"query\":{\"bool\":{\"must\":[{\"match\":{\"legacy_descriptor.legacy_table_name\":{\"query\":\"CLIENT_T\",\"_name\":\"q_cli\"}}},{\"query_string\":{\"default_field\":\"date_of_birth_as_text\",\"query\":\"01171975\",\"boost\":\"14\"}}],\"filter\":[{\"match\":{\"gender\":{\"query\":\"male\",\"_name\":\"q_gender\"}}},{\"match\":{\"sp_county\":{\"query\":\"los angeles\",\"minimum_should_match\":\"100%\",\"_name\":\"q_county\"}}}]}},\"functions\":[{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"1_exact_last\"}}},{\"match\":{\"first_name\":{\"query\":\"jorge\",\"_name\":\"1_exact_first\"}}}]}},\"weight\":8192},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"2_exact_last\"}}},{\"match\":{\"first_name\":{\"query\":\"jorge\",\"_name\":\"2_exact_first\"}}},{\"match\":{\"name_suffix\":{\"query\":\"sr\",\"_name\":\"2_exact_suffix\"}}}]}},\"weight\":16384},{\"filter\":{\"multi_match\":{\"query\":\"gonzalez jorge\",\"operator\":\"and\",\"_name\":\"3_multi_aka\",\"fields\":[\"akas.first_name\",\"akas.last_name\"],\"type\":\"cross_fields\"}},\"weight\":4096},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"4_exact_last\"}}},{\"match\":{\"first_name.diminutive\":{\"query\":\"jorge\",\"_name\":\"4_diminutive_first\"}}}]}},\"weight\":2048},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"5_exact_last\"}}},{\"match\":{\"first_name.phonetic\":{\"query\":\"jorge\",\"_name\":\"5_phonetic_first\"}}}]}},\"weight\":1024},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"6_exact_last\"}}},{\"fuzzy\":{\"first_name\":{\"value\":\"jorge\",\"_name\":\"6_fuzzy_first\",\"fuzziness\":\"3\",\"prefix_length\":\"1\",\"max_expansions\":\"50\"}}}]}},\"weight\":512},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"7_exact_last\"}}},{\"match\":{\"first_name_ngram\":{\"query\":\"jorge\",\"minimum_should_match\":\"10%\",\"_name\":\"7_partial_first\"}}}]}},\"weight\":256},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"8_exact_last\"}}}],\"must_not\":[{\"match\":{\"first_name\":{\"query\":\"jorge\",\"_name\":\"8_no_match_first\"}}}]}},\"weight\":128},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"jorge\",\"_name\":\"9a_reverse_exact_last\"}}},{\"match\":{\"first_name\":{\"query\":\"gonzalez\",\"_name\":\"9a_reverse_exact_first\"}}}]}},\"weight\":64},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"jorge\",\"_name\":\"9b_reverse_partial_last\"}}},{\"match\":{\"first_name_ngram\":{\"query\":\"gonzalez\",\"minimum_should_match\":\"25%\",\"_name\":\"9b_reverse_partial_first\"}}}]}},\"weight\":64},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"10_dupe_exact_last\"}}},{\"match\":{\"first_name\":{\"query\":\"gonzalez\",\"_name\":\"10_dupe_exact_first\"}}}]}},\"weight\":32},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"first_name_ngram\":{\"query\":\"jorge\",\"minimum_should_match\":\"10%\",\"_name\":\"11_partial_first\"}}},{\"match\":{\"last_name_ngram\":{\"query\":\"gonzalez\",\"minimum_should_match\":\"15%\",\"_name\":\"11_partial_last\"}}}]}},\"weight\":16}],\"score_mode\":\"max\",\"boost_mode\":\"max\"}}}";
    final DoraTraceLogSearchEntry entry =
        new DoraTraceLogSearchEntry("BARNEY", "people-summary", json);

    final WebTarget webTarget = mock(WebTarget.class);
    when(client.target(any(String.class))).thenReturn(webTarget);

    final Invocation.Builder builder = mock(Invocation.Builder.class);
    when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);

    final Response response = mock(Response.class);
    when(builder.post(any())).thenReturn(response);

    when(response.getStatus()).thenReturn(Status.CREATED.getStatusCode());
    when(response.readEntity(String.class)).thenReturn(json);

    target.sendSearchQuery(entry);
  }

  @Test
  public void sendSearchQueryStatusBad() throws Exception {
    final String json =
        "{\"size\":\"250\",\"track_scores\":\"true\",\"sort\":[{\"_score\":\"desc\",\"last_name.keyword\":\"asc\",\"first_name.keyword\":\"asc\",\"_uid\":\"desc\"}],\"min_score\":\"2.5\",\"_source\":[\"id\",\"legacy_source_table\",\"first_name\",\"middle_name\",\"last_name\",\"name_suffix\",\"gender\",\"akas\",\"date_of_birth\",\"date_of_death\",\"ssn\",\"languages\",\"races\",\"ethnicity\",\"client_counties\",\"case_status\",\"addresses.id\",\"addresses.effective_start_date\",\"addresses.street_name\",\"addresses.street_number\",\"addresses.city\",\"addresses.county\",\"addresses.state_code\",\"addresses.zip\",\"addresses.type\",\"addresses.legacy_descriptor\",\"addresses.phone_numbers.number\",\"addresses.phone_numbers.type\",\"csec.start_date\",\"csec.end_date\",\"csec.csec_code_id\",\"csec.description\",\"sp_county\",\"sp_phone\",\"legacy_descriptor\",\"highlight\",\"phone_numbers.id\",\"phone_numbers.number\",\"phone_numbers.type\",\"estimated_dob_code\",\"sensitivity_indicator\",\"race_ethnicity\",\"open_case_responsible_agency_code\"],\"highlight\":{\"order\":\"score\",\"number_of_fragments\":\"10\",\"require_field_match\":\"false\",\"fields\":{\"autocomplete_search_bar\":{\"matched_fields\":[\"autocomplete_search_bar\",\"autocomplete_search_bar.phonetic\",\"autocomplete_search_bar.diminutive\"]},\"searchable_date_of_birth\":{}}},\"query\":{\"function_score\":{\"query\":{\"bool\":{\"must\":[{\"match\":{\"legacy_descriptor.legacy_table_name\":{\"query\":\"CLIENT_T\",\"_name\":\"q_cli\"}}},{\"query_string\":{\"default_field\":\"date_of_birth_as_text\",\"query\":\"01171975\",\"boost\":\"14\"}}],\"filter\":[{\"match\":{\"gender\":{\"query\":\"male\",\"_name\":\"q_gender\"}}},{\"match\":{\"sp_county\":{\"query\":\"los angeles\",\"minimum_should_match\":\"100%\",\"_name\":\"q_county\"}}}]}},\"functions\":[{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"1_exact_last\"}}},{\"match\":{\"first_name\":{\"query\":\"jorge\",\"_name\":\"1_exact_first\"}}}]}},\"weight\":8192},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"2_exact_last\"}}},{\"match\":{\"first_name\":{\"query\":\"jorge\",\"_name\":\"2_exact_first\"}}},{\"match\":{\"name_suffix\":{\"query\":\"sr\",\"_name\":\"2_exact_suffix\"}}}]}},\"weight\":16384},{\"filter\":{\"multi_match\":{\"query\":\"gonzalez jorge\",\"operator\":\"and\",\"_name\":\"3_multi_aka\",\"fields\":[\"akas.first_name\",\"akas.last_name\"],\"type\":\"cross_fields\"}},\"weight\":4096},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"4_exact_last\"}}},{\"match\":{\"first_name.diminutive\":{\"query\":\"jorge\",\"_name\":\"4_diminutive_first\"}}}]}},\"weight\":2048},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"5_exact_last\"}}},{\"match\":{\"first_name.phonetic\":{\"query\":\"jorge\",\"_name\":\"5_phonetic_first\"}}}]}},\"weight\":1024},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"6_exact_last\"}}},{\"fuzzy\":{\"first_name\":{\"value\":\"jorge\",\"_name\":\"6_fuzzy_first\",\"fuzziness\":\"3\",\"prefix_length\":\"1\",\"max_expansions\":\"50\"}}}]}},\"weight\":512},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"7_exact_last\"}}},{\"match\":{\"first_name_ngram\":{\"query\":\"jorge\",\"minimum_should_match\":\"10%\",\"_name\":\"7_partial_first\"}}}]}},\"weight\":256},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"8_exact_last\"}}}],\"must_not\":[{\"match\":{\"first_name\":{\"query\":\"jorge\",\"_name\":\"8_no_match_first\"}}}]}},\"weight\":128},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"jorge\",\"_name\":\"9a_reverse_exact_last\"}}},{\"match\":{\"first_name\":{\"query\":\"gonzalez\",\"_name\":\"9a_reverse_exact_first\"}}}]}},\"weight\":64},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"jorge\",\"_name\":\"9b_reverse_partial_last\"}}},{\"match\":{\"first_name_ngram\":{\"query\":\"gonzalez\",\"minimum_should_match\":\"25%\",\"_name\":\"9b_reverse_partial_first\"}}}]}},\"weight\":64},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"gonzalez\",\"_name\":\"10_dupe_exact_last\"}}},{\"match\":{\"first_name\":{\"query\":\"gonzalez\",\"_name\":\"10_dupe_exact_first\"}}}]}},\"weight\":32},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"first_name_ngram\":{\"query\":\"jorge\",\"minimum_should_match\":\"10%\",\"_name\":\"11_partial_first\"}}},{\"match\":{\"last_name_ngram\":{\"query\":\"gonzalez\",\"minimum_should_match\":\"15%\",\"_name\":\"11_partial_last\"}}}]}},\"weight\":16}],\"score_mode\":\"max\",\"boost_mode\":\"max\"}}}";
    final DoraTraceLogSearchEntry entry =
        new DoraTraceLogSearchEntry("BARNEY", "people-summary", json);

    final WebTarget webTarget = mock(WebTarget.class);
    when(client.target(any(String.class))).thenReturn(webTarget);

    final Invocation.Builder builder = mock(Invocation.Builder.class);
    when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);

    final Response response = mock(Response.class);
    when(builder.post(any())).thenReturn(response);

    when(response.getStatus()).thenReturn(Status.UNAUTHORIZED.getStatusCode());
    when(response.readEntity(String.class)).thenReturn(json);

    target.sendSearchQuery(entry);
  }

}

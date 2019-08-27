package gov.ca.cwds.dora.tracelog;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

public class DoraTraceLogSearchEntryTest {

  String userId = "FRED";
  String index = "people-summary";
  String json =
      "{\"size\":\"250\",\"track_scores\":\"true\",\"sort\":[{\"_score\":\"desc\",\"last_name.keyword\":\"asc\",\"first_name.keyword\":\"asc\",\"_uid\":\"desc\"}],\"min_score\":\"2.5\",\"_source\":[\"id\",\"legacy_source_table\",\"first_name\",\"middle_name\",\"last_name\",\"name_suffix\",\"gender\",\"akas\",\"date_of_birth\",\"date_of_death\",\"ssn\",\"languages\",\"races\",\"ethnicity\",\"client_counties\",\"case_status\",\"addresses.id\",\"addresses.effective_start_date\",\"addresses.street_name\",\"addresses.street_number\",\"addresses.city\",\"addresses.county\",\"addresses.state_code\",\"addresses.zip\",\"addresses.type\",\"addresses.legacy_descriptor\",\"addresses.phone_numbers.number\",\"addresses.phone_numbers.type\",\"csec.start_date\",\"csec.end_date\",\"csec.csec_code_id\",\"csec.description\",\"sp_county\",\"sp_phone\",\"legacy_descriptor\",\"highlight\",\"phone_numbers.id\",\"phone_numbers.number\",\"phone_numbers.type\",\"estimated_dob_code\",\"sensitivity_indicator\",\"race_ethnicity\",\"open_case_responsible_agency_code\"],\"highlight\":{\"order\":\"score\",\"number_of_fragments\":\"10\",\"require_field_match\":\"false\",\"fields\":{\"autocomplete_search_bar\":{\"matched_fields\":[\"autocomplete_search_bar\",\"autocomplete_search_bar.phonetic\",\"autocomplete_search_bar.diminutive\"]},\"searchable_date_of_birth\":{}}},\"query\":{\"function_score\":{\"query\":{\"bool\":{\"must\":[{\"match\":{\"legacy_descriptor.legacy_table_name\":{\"query\":\"CLIENT_T\",\"_name\":\"q_cli\"}}}],\"filter\":[]}},\"functions\":[{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name\":{\"query\":\"fred\",\"_name\":\"1_exact\"}}}]}},\"weight\":16384},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"akas.last_name\":{\"query\":\"fred\",\"_name\":\"2_aka\"}}}]}},\"weight\":8192},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name.phonetic\":{\"query\":\"fred\",\"_name\":\"3_phonetic\"}}}]}},\"weight\":1024},{\"filter\":{\"bool\":{\"must\":[{\"match\":{\"last_name_ngram\":{\"query\":\"fred\",\"minimum_should_match\":\"10%\",\"_name\":\"4_partial\"}}}]}},\"weight\":2048},{\"filter\":{\"bool\":{\"must\":[{\"fuzzy\":{\"last_name\":{\"value\":\"fred\",\"_name\":\"5_fuzzy\",\"fuzziness\":\"AUTO\",\"prefix_length\":\"1\",\"max_expansions\":\"150\"}}}]}},\"weight\":4096}],\"score_mode\":\"max\",\"boost_mode\":\"max\"}}}";

  DoraTraceLogSearchEntry target;

  @Before
  public void setup() throws Exception {
    target = new DoraTraceLogSearchEntry(userId, index, json);
  }

  @Test
  public void type() throws Exception {
    assertThat(DoraTraceLogSearchEntry.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getIndex_A$() throws Exception {
    String actual = target.getIndex();
    String expected = index;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getUserId_A$() throws Exception {
    String actual = target.getUserId();
    String expected = userId;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMoment_A$() throws Exception {
    LocalDateTime actual = target.getMoment();
    LocalDateTime expected = LocalDateTime.now();
    assertThat(actual, is(lessThanOrEqualTo(expected)));
  }

  @Test
  public void getJson_A$() throws Exception {
    String actual = target.getJson();
    String expected = json;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void toString_A$() throws Exception {
    String actual = target.toString();
    assertThat(actual, notNullValue());
  }

}

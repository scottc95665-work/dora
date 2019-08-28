package gov.ca.cwds.rest.filters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class DelegateServletInputStream extends ServletInputStream {

  final ByteArrayInputStream byteStream = new ByteArrayInputStream(
      "{\"size\":\"250\",\"track_scores\":\"true\",\"sort\":[{\"_score\":\"desc\",\"last_name.keyword\":\"asc\",\"first_name.keyword\":\"asc\",\"_uid\":\"desc\"}],\"min_score\":\"2.5\",\"_source\":[\"id\",\"legacy_source_table\",\"first_name\",\"middle_name\",\"last_name\",\"name_suffix\",\"gender\",\"akas\",\"date_of_birth\",\"date_of_death\",\"ssn\",\"languages\",\"races\",\"ethnicity\",\"client_counties\",\"case_status\",\"addresses.id\",\"addresses.effective_start_date\",\"addresses.street_name\",\"addresses.street_number\",\"addresses.city\",\"addresses.county\",\"addresses.state_code\",\"addresses.zip\",\"addresses.type\",\"addresses.legacy_descriptor\",\"addresses.phone_numbers.number\",\"addresses.phone_numbers.type\",\"csec.start_date\",\"csec.end_date\",\"csec.csec_code_id\",\"csec.description\",\"sp_county\",\"sp_phone\",\"legacy_descriptor\",\"highlight\",\"phone_numbers.id\",\"phone_numbers.number\",\"phone_numbers.type\",\"estimated_dob_code\",\"sensitivity_indicator\",\"race_ethnicity\",\"open_case_responsible_agency_code\"],\"highlight\":{\"order\":\"score\",\"number_of_fragments\":\"10\",\"require_field_match\":\"false\",\"fields\":{\"autocomplete_search_bar\":{\"matched_fields\":[\"autocomplete_search_bar\",\"autocomplete_search_bar.phonetic\",\"autocomplete_search_bar.diminutive\"]},\"searchable_date_of_birth\":{}}},\"query\":{\"bool\":{\"must\":[{\"match\":{\"legacy_descriptor.legacy_ui_id_flat\":{\"query\":\"1406607661170082074\",\"boost\":\"14\"}}}]}}}"
          .getBytes(Charset.defaultCharset()));

  @Override
  public boolean isFinished() {
    return !(byteStream.available() > 0);
  }

  @Override
  public boolean isReady() {
    return (byteStream.available() > 0);
  }

  @Override
  public void setReadListener(ReadListener readListener) {}

  @Override
  public int read() throws IOException {
    return byteStream.read();
  }

  @Override
  public int read(byte[] b) throws IOException {
    return byteStream.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return byteStream.read(b, off, len);
  }

  @Override
  public long skip(long n) throws IOException {
    return byteStream.skip(n);
  }

  @Override
  public int available() throws IOException {
    return byteStream.available();
  }

  @Override
  public void close() throws IOException {
    byteStream.close();
  }

  @Override
  public synchronized void mark(int readlimit) {
    byteStream.mark(readlimit);
  }

  @Override
  public synchronized void reset() throws IOException {
    byteStream.reset();
  }

  @Override
  public boolean markSupported() {
    return byteStream.markSupported();
  }

}

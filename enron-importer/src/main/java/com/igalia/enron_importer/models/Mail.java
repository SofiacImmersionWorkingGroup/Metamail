package com.igalia.enron_importer.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * This class represents a single entity in the enron dataset.
 *
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 */
public class Mail {

  private String id;
  private String body;
  private Map<String, String> headers;

  /**
   * Default constructor, sets everything to empty String.
   *
   */
  public Mail() {
    this("", new HashMap<String, String>());
  }

  /**
   * Constructor. Use this if creating object.
   *
   * @param body The rest of the message, unfiltered
   */
  public Mail(String body, Map<String, String> headers) {
    this.id = UUID.randomUUID().toString();
    this.body = body;
    this.headers = copyHeaders(headers);
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, String> getHeaders() {
    return this.headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = copyHeaders(headers);
  }

  private static Map<String, String> copyHeaders(Map<String, String> headers) {
    Map<String, String> newHeaders = new HashMap<String, String>();
    for (String header: headers.keySet()) {
      newHeaders.put(header, headers.get(header));
    }
    return newHeaders;
  }

  @Override
  public String toString() {
    return String.format("Mail{id=%s, body=%s}", this.id, 
        this.body.substring(0, (body.length() >= 16 ? 16 : body.length())));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (!(obj instanceof Mail)) {
      return false;
    } else {
      Mail other = (Mail) obj;
      return Objects.equals(this.id, other.id) &&
        Objects.equals(this.body, other.body);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, body);
  }
}

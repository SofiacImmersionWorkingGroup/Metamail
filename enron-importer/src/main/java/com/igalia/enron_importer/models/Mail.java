/**
 * @author Diego Pino García <dpino@igalia.com>
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 *
 * This class represents a single entity in the enron dataset.
 */
package com.igalia.enron_importer.models;

import org.apache.commons.lang.StringUtils;

import java.util.UUID;

public class Mail {

  private String id;
  private String person;
  private String folder;
  private String body;

  public Mail() {
  }

  public static Mail create(String person, String folder, String body) {
    return create(UUID.randomUUID().toString(), person, folder, body);
  }

  public static Mail create(String id, String person, String folder, String body) {
    Mail result = new Mail();
    result.id = id;
    result.folder = folder;
    result.person = person;
    result.body = body;
    return result;
  }

  public String getPerson() {
    return person;
  }

  public String getFolder() {
    return folder;
  }

  public String getBody() {
    return body;
  }

  public String getFirstLine() {
    return body.substring(0, body.indexOf('\n'));
  }

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return String.format("(id: %s; person: %s; folder: %s; body: %s)", id, person,
        folder, StringUtils.substring(body, 0, 16));
  }
}

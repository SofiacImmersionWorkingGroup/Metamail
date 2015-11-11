/**
 * @author Diego Pino García <dpino@igalia.com>
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 *
 * This class represents a single entity in the enron dataset.
 */
package com.igalia.enron_importer.models;

import java.util.UUID;

public class Mail {

  private String id;
  private String person;
  private String folder;
  private String body;

  public Mail(String person, String folder, String body) {
    this.id = UUID.randomUUID().toString();
    this.person = person;
    this.folder = folder;
    this.body = body;
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

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return String.format("(id: %s; person: %s; folder: %s; body: %s)", id, person,
        folder, body.substring(0, (body.length() >= 16 ? 16: body.length())));
  }
}

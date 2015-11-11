/**
 * @author Diego Pino García <dpino@igalia.com>
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 *
 * This class represents a single entity in the enron dataset.
 */
package com.igalia.enron_importer.models;

import com.google.common.base.MoreObjects;

import java.util.Objects;
import java.util.UUID;

public class Mail {

  private String id;
  private String person;
  private String folder;
  private String body;

  public Mail() {
    this("", "", "");
  }

  public Mail(String person, String folder, String body) {
    this.id = UUID.randomUUID().toString();
    this.person = person;
    this.folder = folder;
    this.body = body;
  }

  public String getPerson() {
    return person;
  }

  public void setPerson(String person) {
    this.person = person;
  }

  public String getFolder() {
    return folder;
  }

  public void setFolder(String folder) {
    this.folder = folder;
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

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("id", this.id)
      .add("person", this.person)
      .add("folder", this.folder)
      .add("body", this.body.substring(0, (body.length() >= 16 ? 16 : body.length())))
      .toString();
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
        Objects.equals(this.person, other.person) &&
        Objects.equals(this.folder, other.folder) &&
        Objects.equals(this.body, other.body);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, person, folder, body);
  }
}

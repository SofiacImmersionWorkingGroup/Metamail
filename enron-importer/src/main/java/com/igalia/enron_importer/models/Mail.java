/**
 * This class represents a single entity in the enron dataset.
 *
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 */
package com.igalia.enron_importer.models;

//import com.google.common.base.MoreObjects;

import java.util.Objects;
import java.util.UUID;
import java.util.List;
import java.util.Enumeration;

import java.io.ByteArrayInputStream;

import javax.mail.internet.MimeBodyPart;
import javax.mail.*;

import com.sun.mail.util.MessageRemovedIOException;

public class Mail {

  private String id;
  private String person;
  private String folder;
  private String body;
  private MimeBodyPart mimebody;
  private List headers;

  /**
   * Default constructor, sets everything to empty String.
   *
   */
  public Mail() {
    this("", "", "");
  }

  /**
   * Constructor. Use this if creating object.
   *
   * @param person Recipient of the email
   * @param folder Their mail folder that it was in
   * @param body The rest of the message, unfiltered
   */
  public Mail(String person, String folder, String body) {
    this.id = UUID.randomUUID().toString();
    this.person = person;
    this.folder = folder;
    this.body = body;
    try {
      this.mimebody = new MimeBodyPart(new ByteArrayInputStream(body.getBytes()));
      //this.headers = this.mimebody.getAllHeaders();
      for (Enumeration<Header> e = this.mimebody.getAllHeaders(); e.hasMoreElements();) {
        Header element = e.nextElement();
        System.out.println(element.getName());
        System.out.println(element.getValue());
      } 
    } catch (MessagingException e) {
      System.out.println(e);
    } 

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
    return "To be implemented";
    /*
    return Objects.toStringHelper(this)
      .add("id", this.id)
      .add("person", this.person)
      .add("folder", this.folder)
      .add("body", this.body.substring(0, (body.length() >= 16 ? 16 : body.length())))
      .toString();
    */
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

/** * This class represents a single entity in the enron dataset.
 *
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 */
package com.igalia.enron_importer.models;

import com.sun.mail.util.MessageRemovedIOException;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.mail.internet.MimeBodyPart;
import javax.mail.*;

public class Mail {

  private String id;
  private String body;
  private MimeBodyPart mimebody;
  private HashMap<String, String> headers;

  /**
   * Default constructor, sets everything to empty String.
   *
   */
  public Mail() {
    this("");
  }

  /**
   * Constructor. Use this if creating object.
   *
   * @param body The rest of the message, unfiltered
   */
  public Mail(String body) {
    this.id = UUID.randomUUID().toString();
    this.body = body;
    this.headers = new HashMap<String, String>();
    try {
      this.mimebody = new MimeBodyPart(new ByteArrayInputStream(body.getBytes()));
      for (Enumeration<Header> e = this.mimebody.getAllHeaders(); e.hasMoreElements();) {
        Header element = e.nextElement();
        this.headers.put(element.getName(), element.getValue());
      } 
    } catch (MessagingException e) {
      System.out.println(e);
    } 

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

  public Set<String> getAllHeaders() {
    return this.headers.keySet();
  }

  public boolean hasHeader(String header) {
    return this.headers.containsKey(header);
  }

  public String getHeader(String header) {
    return this.headers.get(header);
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

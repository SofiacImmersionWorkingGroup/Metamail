/**
 * This class is meant to parse a raw file from the dataset into the
 * mail object.
 *
 * @author Diego Pino García <dpino@igalia.com>
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 */
package com.igalia.enron_importer.services;

import com.igalia.enron_importer.models.Mail;
import com.igalia.enron_importer.models.MailParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

@Service
public class MailFactory {

  private static Logger LOG = LoggerFactory.getLogger(MailFactory.class);

  /**
   * This method takes in the file and attempts to parse it into a Mail object.
   *
   * @param infile The file to parse
   * @return the parsed Mail object from the input file
   * @throws MailParseException when the file is not in the right folder format
   * @throws IOException during any other Java I/O failures.
   */
  public Mail createMail(File infile) throws MailParseException, IOException {
    if (infile == null) {
      throw new MailParseException("NULL");
    }
    Map<String, String> headers = new HashMap<String, String>();
    Header next = null;
    MimeBodyPart mimebody = null;
    Enumeration<Header> headerEnum = null;
    String body = "";
    try {
      mimebody = new MimeBodyPart(new FileInputStream(infile));
      body = (String) mimebody.getContent();
      headerEnum = mimebody.getAllHeaders();
      while(headerEnum.hasMoreElements()) {
        next = headerEnum.nextElement();
        if (!next.getValue().isEmpty()) {
          headers.put(next.getName(), next.getValue());
        } else {
          LOG.debug("Skipping empty header with name '{}'", next.getName());
        }
      }

    } catch (MessagingException e) {
      throw new MailParseException(infile.getAbsolutePath());
    } catch (FileNotFoundException e) {
      throw new MailParseException(infile.getAbsolutePath());
    }

    return new Mail(body, headers);
  }
}


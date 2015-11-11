/**
 *
 * @author Diego Pino García <dpino@igalia.com>
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 *
 */
package com.igalia.enron_importer.services;

import com.igalia.enron_importer.models.Mail;
import com.igalia.enron_importer.models.MailParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
public class MailFactory {

  private static Logger LOG = LoggerFactory.getLogger(MailFactory.class);

  public Mail createMail(String filename) throws MailParseException {
    BufferedReader infile = null;
    StringBuffer bodyBuff = new StringBuffer();
    String[] parts = filename.split("/");

    if (parts.length < 3) {
      throw new MailParseException(filename);

    } else {
      try {
        String line = "";
        infile = new BufferedReader(new FileReader(filename));
        while ((line = infile.readLine()) != null) {
          bodyBuff.append(line);
        }
      } catch (IOException e) {
        LOG.error("%s", e);

      } finally {
        if (infile != null) {
          try {
            infile.close();
          } catch (IOException e) {
            LOG.error("%s", e);
          }
        }
      }
    }

    return new Mail(parts[parts.length - 3], parts[parts.length - 2], bodyBuff.toString());
  }
}


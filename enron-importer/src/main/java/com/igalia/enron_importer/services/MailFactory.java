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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
    BufferedReader inBuffer = null;
    StringBuffer bodyBuff = new StringBuffer();
    String[] parts = infile.getAbsolutePath().split("/");

    /*
     * We are expecting the mail directories to be as follows:
     *
     * PERSON/FOLDER/mailFile
     *
     * Where person and folder are parsed into the Mail object, and 
     * the mailFile contains the body of the mail object.
     *
     * TODO make this more generic to other datasets.
     */
    if (parts.length < 3) {
      throw new MailParseException(infile.getAbsolutePath());

    } else {
      try {
        String line = "";
        inBuffer = new BufferedReader(new FileReader(infile));

        // Read the file through our reader into our string buffer.
        while ((line = inBuffer.readLine()) != null) {
          bodyBuff.append(line);
          // add back in the newline so we can process the body later
          bodyBuff.append(System.lineSeparator());
        }
      // No catch here for IOException since we want to throw them.
      } catch (FileNotFoundException e) {
        throw new MailParseException(infile.getAbsolutePath());
      // but we do want to make sure that the reader is closed properly.
      } finally {
        if (inBuffer != null) {
          try {
            inBuffer.close();
          } catch (IOException e) {
            LOG.error("{}", e);
          }
        }
      }
    }

    // Remember the format PERSON/FOLDER/mailFile
    return new Mail(parts[parts.length - 3], parts[parts.length - 2], bodyBuff.toString());
  }
}


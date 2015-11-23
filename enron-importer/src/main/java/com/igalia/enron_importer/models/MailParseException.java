/**
 * This Exception extension is so that we can track when we fail to parse a specific
 * message file from the dataset. It reports based on the file name (hopefully including paths).
 *
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 */
package com.igalia.enron_importer.models;

public class MailParseException extends Exception {

  /**
   * Default constructor. Uses super() with a specific message
   *
   * @param filename The name of the email file name that failed.
   */
  public MailParseException (String filename) {
    super(String.format("Mail file is not in the correct format, failure on parsing %s", filename));
  }
}

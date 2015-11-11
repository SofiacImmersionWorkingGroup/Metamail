package com.igalia.enron_importer.models;

public class MailParseException extends Exception {

  public MailParseException (String filename) {
    super(String.format("maildir is not in the correct format, failure on parsing %s", filename));
  }
}

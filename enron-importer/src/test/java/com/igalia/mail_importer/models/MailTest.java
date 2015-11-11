package com.igalia.enron_importer.models;

import mockit.Expectations;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MailTest {

  private static final int BODY_TRUNCATE_LEN = 16;
  private static final String DEFAULT_ID = "1";

  @Tested
  private Mail mail;

  @Before
  public void setup() {
    mail = new Mail();
    mail.setId(DEFAULT_ID);
  }

  private void verify_toString(final String expected) {
    new Verifications() {{
      Assert.assertEquals("The object's toString is incorrect.", expected, mail.toString());
    }};
  }

  @Test
  public void toString_Default() {
    verify_toString(String.format("Mail{id=%s, person=, folder=, body=}", DEFAULT_ID));
  }

  @Test
  public void toString_BodyEdgeLength() {
    String bodyEdgeLen = "IthinkThisIsLong";
    mail.setBody(bodyEdgeLen);
    verify_toString(String.format("Mail{id=%s, person=, folder=, body=%s}", DEFAULT_ID, bodyEdgeLen));
  }

  @Test
  public void toString_BodyBigLength() {
    String bodyBiglen = "IthinkThisIsLongEnough";
    mail.setBody(bodyBiglen);
    verify_toString(String.format("Mail{id=%s, person=, folder=, body=%s}", DEFAULT_ID,
          bodyBiglen.substring(0, BODY_TRUNCATE_LEN)));
  }
}

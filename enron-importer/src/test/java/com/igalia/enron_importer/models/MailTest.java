package com.igalia.enron_importer.models;

import mockit.Expectations;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

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

  private void verify_equals(final Object expected, final Object actual) {
    new Verifications() {{
      Assert.assertEquals("The objects compared are not equal.", expected, actual);
    }};
  }

  private void verify_toString(final String expected) {
    verify_equals(expected, mail.toString());
  }
  private void verify_hashCode(final int expected) {
    verify_equals(expected, mail.hashCode());
  }
  private void verify_equalsTrue(final Object other) {
    new Verifications() {{
      Assert.assertTrue("The object's equals was not true.", mail.equals(other));
    }};
  }

  private void verify_equalsFalse(final Object other) {
    new Verifications() {{
      Assert.assertFalse("The object's equals was not false.", mail.equals(other));
    }};
  }

  @Test
  public void toString_Default() {
    verify_toString(String.format("Mail{id=%s, body=}", DEFAULT_ID));
  }

  @Test
  public void toString_BodyEdgeLength() {
    String bodyEdgeLen = "IthinkThisIsLong";
    mail.setBody(bodyEdgeLen);
    verify_toString(String.format("Mail{id=%s, body=%s}", DEFAULT_ID, bodyEdgeLen));
  }

  @Test
  public void toString_BodyBigLength() {
    String bodyBiglen = "IthinkThisIsLongEnough";
    mail.setBody(bodyBiglen);
    verify_toString(String.format("Mail{id=%s, body=%s}", DEFAULT_ID,
          bodyBiglen.substring(0, BODY_TRUNCATE_LEN)));
  }

  @Test
  public void equals_NullParameter() {
    verify_equalsFalse(null);
  }

  @Test
  public void equals_AnotherClass() {
    verify_equalsFalse("ThisShouldn'tBeAString.");
  }

  @Test
  public void equals_ChangedBody() {
    Mail other = new Mail("bodyChanged");
    other.setId(DEFAULT_ID);
    verify_equalsFalse(other);
  }

  @Test
  public void equals_Normal() {
    Mail other = new Mail();
    other.setId(DEFAULT_ID);
    verify_equalsTrue(other);
  }

  @Test
  public void hashCode_Normal() {
    int expected = Objects.hash(DEFAULT_ID, "");
    verify_hashCode(expected);
  }
}

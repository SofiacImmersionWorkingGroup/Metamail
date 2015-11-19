package com.igalia.enron_importer.services;

import com.igalia.enron_importer.models.Mail;
import com.igalia.enron_importer.models.MailParseException;

import mockit.Tested;
import mockit.Verifications;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MailFactoryTest {

  private static final String TEST_FILE_NAME = "testfile.txt";
  private static final String TEST_FILE_CONTENTS = "IthinkThisIsLongEnoughForABody";
  private static final String DEFAULT_ID = "1";

  private static final Logger LOG = LoggerFactory.getLogger(MailFactoryTest.class);

  @ClassRule
  public static TemporaryFolder folder = new TemporaryFolder();

  @Tested
  private MailFactory mailFactory;

  @BeforeClass
  public static void setup() throws IOException {
    // Set up the test file to be used in the temp folder.
    File testFile = folder.newFile(TEST_FILE_NAME);
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(testFile));
      writer.write(String.format("%s\n", TEST_FILE_CONTENTS));
      
    } catch(IOException e) {
      LOG.error("Couldn't create the test file... Tests are bound to fail.");
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
      } catch (IOException e) {
        LOG.error("%s", e);
      }
    }
    LOG.debug("Temporary file has been written.");
  }

  private void verify_equals(final Object expected, final Object actual) {
    new Verifications() {{
      Assert.assertEquals("The created object is incorrect.", expected, actual);
    }};
  }

  @Test(expected=MailParseException.class)
  public void createMail_NullParameter() throws MailParseException, IOException {
    Mail actual = mailFactory.createMail(null);
  }

  @Test(expected=MailParseException.class)
  public void createMail_NoFileAtPath() throws MailParseException, IOException {
    Mail actual = mailFactory.createMail(new File("1/2/3"));
  }

  @Test
  public void createMail_Normal() throws MailParseException, IOException {
    final Mail expected = new Mail(TEST_FILE_CONTENTS+System.lineSeparator());
    expected.setId(DEFAULT_ID);

    final Mail actual = mailFactory.createMail(new File(folder.getRoot() + "/" + TEST_FILE_NAME));
    actual.setId(DEFAULT_ID);

    verify_equals(expected, actual);
  }

  @Test
  public void createMail_NoContents() throws MailParseException, IOException {
    File emptyFile = folder.newFile("emptyFile");
    final Mail expected = new Mail("");
    expected.setId(DEFAULT_ID);

    final Mail actual = mailFactory.createMail(emptyFile);
    actual.setId(DEFAULT_ID);

    verify_equals(expected, actual);
  }
}

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

  private static final String TEST_PERSON = "testperson";
  private static final String TEST_FOLDER = "testfolder";
  private static final String TEST_FILE_NAME = TEST_PERSON+"/"+TEST_FOLDER+"/testfile.txt";
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
    folder.newFolder(TEST_PERSON);
    folder.newFolder(TEST_PERSON, TEST_FOLDER);
    File testFile = folder.newFile(TEST_FILE_NAME);
    LOG.debug("Used the TempFolder to create my file space to use with tests.");

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
  public void createMail_NoPath() throws MailParseException, IOException {
    Mail actual = mailFactory.createMail("");
  }

  @Test(expected=MailParseException.class)
  public void createMail_EdgePath() throws MailParseException, IOException {
    Mail actual = mailFactory.createMail("1/2");
  }

  @Test(expected=IOException.class)
  public void createMail_NoFileAtPath() throws MailParseException, IOException {
    Mail actual = mailFactory.createMail("1/2/3");
  }

  @Test
  public void createMail_Normal() throws MailParseException, IOException {
    final Mail expected = new Mail(TEST_PERSON, TEST_FOLDER, TEST_FILE_CONTENTS);
    expected.setId(DEFAULT_ID);

    final Mail actual = mailFactory.createMail(folder.getRoot() + "/" + TEST_FILE_NAME);
    actual.setId(DEFAULT_ID);

    verify_equals(expected, actual);
  }

  @Test
  public void createMail_NoContents() throws MailParseException, IOException {
    String emptyFileName = TEST_PERSON+"/"+TEST_FOLDER+"/emptyFile";
    File emptyFile = folder.newFile(emptyFileName);
    final Mail expected = new Mail(TEST_PERSON, TEST_FOLDER, "");
    expected.setId(DEFAULT_ID);

    final Mail actual = mailFactory.createMail(emptyFile.getAbsolutePath());
    actual.setId(DEFAULT_ID);

    verify_equals(expected, actual);
  }
}

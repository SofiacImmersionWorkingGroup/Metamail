/**
 * This class serves as the main execution of the enron import.
 *
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 */
package com.igalia.enron_importer.thread;

import com.igalia.enron_importer.models.Mail;
import com.igalia.enron_importer.models.MailParseException;
import com.igalia.enron_importer.services.MailFactory;
import com.igalia.enron_importer.services.MailRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.Resource;

public class EnronImportThread implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(EnronImportThread.class);

  @Resource
  private MailFactory mailFactory;

  @Resource
  private MailRepository mailRepository;

  @Resource(name="maildir")
  private File maildir;

  @Resource(name="mailBufferSize")
  private Integer mailBufferSize;

  @Override
  public void run() {
    Mail mail = null;
    List<Mail> mailBuffer = new ArrayList<Mail>();
    int mailCount = 0;
    int totalFileCount = 0;
    File tempFile = null;
    Queue<File> files = new LinkedBlockingQueue<File>();

    if (maildir.listFiles().length > 0) {
      Collections.addAll(files, maildir.listFiles());
    } else {
      // Loop will not execute, the files queue is empty.
    }

    /* 
     * Run through the entire queue, and add any if directories are found.
     * This is the iterative approach instead of the recursive, meaning
     * the stack/heap won't grow more, and we are guaranteed that we touch each
     * file and directory only once O(n+k). n=files, k=directories.
     */
    while(!files.isEmpty()) {
      tempFile = files.poll();

      // If directory, explore and add to the queue
      if (tempFile.isDirectory()) {
        Collections.addAll(files, tempFile.listFiles());
        LOG.debug("Found a new directory: '{}/{}'", tempFile.getAbsolutePath(), tempFile.getName());
        continue;

      // If file, then expand it and capture its data.
      } else if (tempFile.isFile()) {
        totalFileCount++;

        try {
          mail = mailFactory.createMail(tempFile);
          System.out.println("Found: " + tempFile);

        } catch (MailParseException e) {
          // Means we couldn't parse the file, so just skip it for now.
          LOG.warn("{}", e);
          continue; 
        } catch (IOException e) {
          // Means something else happened with the Java IO layer, skip it.
          LOG.warn("{}", e);
          continue; 
        }

        // Add to our list, even if body is empty... Mail api will change soon.
        mailBuffer.add(mail);

        // Check buffer size to add to the Hbase instance
        if (mailBuffer.size() >= mailBufferSize) {
          mailCount += mailRepository.batchMailPut(mailBuffer);
          mailBuffer.clear();
          LOG.debug("Just added {} mail items to the hbase instance", mailCount);
        }
      }
    }
    LOG.info("Finsihed! total files found: {}, total imported: {}.", totalFileCount, mailCount);
  }
}

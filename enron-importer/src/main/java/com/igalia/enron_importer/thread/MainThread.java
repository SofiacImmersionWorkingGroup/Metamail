package com.igalia.enron_importer.thread;

import com.igalia.enron_importer.services.MailFactory;
import com.igalia.enron_importer.services.MailRepository;

import javax.annotation.Resource;

public class MainThread implements Runnable {

  @Resource
  private MailFactory mailFactory;

  @Resource
  private MailRepository mailRepository;

  @Resource(name="maildir")
  private String maildir;

  @Override
  public void run() {
    System.out.println("Hello world");
  }
}


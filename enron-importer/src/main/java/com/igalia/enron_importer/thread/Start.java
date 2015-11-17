/**
 * Main injection point for the JAR method of running this service. It will
 * pick up whichever bean is named 'MainThread' from the spring configuration
 * and run it as though it was a Runnable class. This allows us to change on the fly
 * the run type of the application based on configuration instead of having to
 * recompile the entire application.
 *
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 * @version 1.0
 *
 */
package com.igalia.enron_importer.thread;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Start {

  /*
   * Main method
   */
  public static void main (String[] args) {  
    AbstractApplicationContext appContext = null;

    try {
      appContext = new ClassPathXmlApplicationContext("applicationContext.xml");

      Runnable start = (Runnable) appContext.getBean("MainThread");
      start.run();

    } catch (Throwable t) {
      throw new RuntimeException(t);
    } finally {
      if (appContext != null) {
        appContext.registerShutdownHook();
        appContext.close();
      }
    }
  }
}


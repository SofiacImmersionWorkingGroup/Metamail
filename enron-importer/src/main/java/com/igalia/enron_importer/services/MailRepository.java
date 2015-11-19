/**
 * This class serves as the interface to the Hbase database.
 *
 * It is using the Spring-data-hbase library to generate its
 * calls. Currently, it only supports adding the 'Mail' class
 * in a batch format, but when we require more support and functionality,
 * we can add it. This is so that we can speed up processing without
 * opening the table connection more than required. 
 *
 * Buffer size of the list should be handled external to this service 
 * however; depending on the data being parsed, the heap space may overflow, 
 * so be careful to adjust heap space and buffer size (per mail object).
 *
 * @author Dustin Saunders <dustin.saunders@sofiac.us>
 */
package com.igalia.enron_importer.services;

import com.igalia.enron_importer.models.Mail;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;

@Repository
public class MailRepository {

  private static final Logger LOG = LoggerFactory.getLogger(MailRepository.class);

  @Resource(name="hbaseTemplate")
  private HbaseTemplate hbaseTemplate;

  @Resource(name="tableName")
  private String tableName;

  /**
   * Updates the Hbase database with enron data in a batch format.
   *
   * This method generates a Put request per Mail in the list parameter, and
   * then sends the List of put requests to the Spring library to handle the
   * requests.
   *
   * @param mailList the list of mail objects to add to the database
   * @return the number of objects that were added to the database.
   */
  public int batchMailPut(final List<Mail> mailList) {
    return hbaseTemplate.execute(tableName, new TableCallback<Integer>() {
      @Override
      public Integer doInTable(HTableInterface table) throws Throwable {
        List<Put> putList = new ArrayList<Put>();
        int imported = 0;
        Put put = null;
        for (Mail mail: mailList) {
          Set<String> headers = mail.getAllHeaders();
          put = new Put(Bytes.toBytes(mail.getId()));
          put.add(Bytes.toBytes("body"), Bytes.toBytes(""), Bytes.toBytes(mail.getBody()));
          for (String header : headers) {
            put.add(Bytes.toBytes("body"), Bytes.toBytes(header), Bytes.toBytes(mail.getHeader(header)));
          }
          putList.add(put);
          LOG.trace("Added one Put request for object {}.", mail.getId());
          imported++;
        }
        table.put(putList);
        return imported;
      }
    });
  }
}

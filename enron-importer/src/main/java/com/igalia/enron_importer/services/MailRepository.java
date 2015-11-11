package com.igalia.enron_importer.services;

import com.igalia.enron_importer.models.Mail;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

@Repository
public class MailRepository {

  @Resource(name="hbaseTemplate")
  private HbaseTemplate hbaseTemplate;

  @Resource(name="tableName")
  private String tableName;

  public int batchMailPut(final List<Mail> mailList) {
    return hbaseTemplate.execute(tableName, new TableCallback<Integer>() {
      @Override
      public Integer doInTable(HTableInterface table) throws Throwable {
        List<Put> putList = new ArrayList<Put>();
        int imported = 0;
        Put put = null;
        for (Mail mail: mailList) {
          put = new Put(Bytes.toBytes(mail.getId()));
          put.add(Bytes.toBytes("person"), Bytes.toBytes(""), Bytes.toBytes(mail.getPerson()));
          put.add(Bytes.toBytes("folder"), Bytes.toBytes(""), Bytes.toBytes(mail.getFolder()));
          put.add(Bytes.toBytes("body"), Bytes.toBytes(""), Bytes.toBytes(mail.getBody()));
          putList.add(put);
          imported++;
        }
        table.put(putList);
        return imported;
      }
    });
  }
}

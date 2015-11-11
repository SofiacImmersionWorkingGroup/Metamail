package com.igalia.enron_importer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import com.igalia.enron_importer.models.Mail;
import com.igalia.enron_importer.models.MailParseException;
import com.igalia.enron_importer.services.MailFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Diego Pino García <dpino@igalia.com>
 *
 */
public class Main {
	
    private static final String MAIL_FOLDER = "./data/maildir/";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    

    /**
     *
     * @author Diego Pino García <dpino@igalia.com>
     *
     */
    static class HBaseHelper {

        private static Configuration conf = HBaseConfiguration.create();

        public static HBaseHelper create() throws MasterNotRunningException, ZooKeeperConnectionException , IOException {
            HBaseHelper result = new HBaseHelper();
            result.hbase = new HBaseAdmin(conf);
            return result;
        }

        private HBaseAdmin hbase;

        static {
            conf.set("hbase.master","localhost:60000");
        }

        private HBaseHelper() {

        }

        public void createTable(String tableName, String... descriptors)
                throws IOException {
            if (tableExists(tableName)) {
                dropTable(tableName);
            }
            doCreateTable(tableName, descriptors);
        }

        private void doCreateTable(String tableName, String... descriptors)
                throws IOException {
            HTableDescriptor descriptor = new HTableDescriptor(tableName);
            for (String each : descriptors) {
                HColumnDescriptor cd = new HColumnDescriptor(each.getBytes());
                descriptor.addFamily(cd);
            }
            hbase.createTable(descriptor);
            debug(String.format("Database %s created", tableName));
        }

        public void dropTable(String tableName) throws IOException {
            hbase.disableTable(tableName);
            hbase.deleteTable(tableName);
        }

        public void insert(String tableName, String rowKey, List<String> values)
                throws IOException {

            Connection connection = ConnectionFactory.createConnection(conf);
            Table table = connection.getTable(TableName.valueOf(tableName));
            try {
              if (values.size() == 3) {
                  Put put = new Put(Bytes.toBytes(rowKey));
                  put.add(Bytes.toBytes(values.get(0)),
                        Bytes.toBytes(values.get(1)),
                        Bytes.toBytes(values.get(2)));
                  table.put(put);
              }
            } finally {
              table.close();
              connection.close();
            }
        }

        public boolean tableExists(String tableName) throws IOException {
            return hbase.tableExists(tableName);
        }
    }

    private static void debug(Object obj) {
        System.out.println(String.format("### DEBUG: %s", obj.toString()));
    }
   
  private static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main( String[] args )
    {
        Mail mail;
        MailFactory mailFactory = new MailFactory();
        long failed = 0, imported = 0;
        String tableName = "enron";
        File dir = new File(args.length == 0 ? MAIL_FOLDER : args[0]);
        try {
            HBaseHelper hbase = HBaseHelper.create();
            hbase.createTable(tableName, "person", "folder", "body");
            
            Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.TRUE, TrueFileFilter.TRUE);            
            for (File each: files) {
            	String filename = each.getCanonicalPath();
                try {
              	  mail = mailFactory.createMail(filename);
                } catch (MailParseException e) {
                  LOG.error("%s", e);
                  continue;
                }
            	
            	String body = mail.getBody();
            	if (body != null && !body.isEmpty()) {
            		// System.out.println("### Insert mail: " + mail);
                	hbase.insert(tableName, mail.getId(), Arrays.asList("person", "", mail.getPerson()));
                	hbase.insert(tableName, mail.getId(), Arrays.asList("folder", "", mail.getFolder()));
                	hbase.insert(tableName, mail.getId(), Arrays.asList("body", "", body));                	
                	imported++;
            	} else {
            		failed++;
            	}
            }
			System.out.println(String.format(
					"Total: %d; Imported: %d; Failed: %d", files.size(),
					imported, failed));
        } catch (MasterNotRunningException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

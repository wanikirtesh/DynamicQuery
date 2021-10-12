package com.sbn.misc.dynamic;

import com.sbn.utills.ConnectionManager;
import com.sbn.utills.XlsxReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by idnkiw on 6/17/2016.
 */
public class QueryRunner {
    private static final Logger LOGGER = LogManager.getLogger(QueryRunner.class);
    ExecutorService executor;

    public static void main(String[] args) throws Exception {
        int connectPerThread = 50;
        String excelFilePath = "C:\\Users\\idnkiw\\myExcel2.xlsx";
        XlsxReader xlsxReader = new XlsxReader(excelFilePath);
        List<List<String>> listOfList = new ArrayList<>();
        int conCounter = 1;
        List<String> threadGroup = new ArrayList<>();

        for (int i = 1; i <= xlsxReader.getRowCount("dbs"); i++) {
            threadGroup.add(xlsxReader.getCellStringData("dbs", i, 0) + "~" + xlsxReader.getCellStringData("dbs", i, 1).replace(".0",""));
            if (conCounter > 50) {
                List<String> temp = new ArrayList<>();
                temp.addAll(threadGroup);
                listOfList.add(temp);
                threadGroup.clear();
                conCounter = 1;
            }
            conCounter++;
        }
        if(conCounter>2){
            List<String> temp = new ArrayList<>();
            temp.addAll(threadGroup);
            listOfList.add(temp);
            threadGroup.clear();
        }
        new QueryRunner().spaunConnections(listOfList);
    }

    private void spaunConnections(List<List<String>> mapList) {
        executor = Executors.newFixedThreadPool(mapList.size());
        for (List<String> conMap : mapList) {
            Runnable thread = new runJob(conMap);
            executor.execute(thread);

        }
        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {

        }
        System.out.println("\nFinished all threads");
        ConnectionManager cm;
        try {
            cm = new ConnectionManager("us1s3db04.ideasstg.int", "62001", "sa", "IDeaS123");
            cm.dropConnection();
        }catch (Exception e){}
    }

    public static class runJob implements Runnable {
        final List<String> conMap;

        public void run() {
            for (String keys : conMap) {
                executeDeFragment(keys);
            }
        }

        runJob(List<String> conMap) {
            this.conMap = conMap;
        }

        private void executeDeFragment(String keys) {
            String[] vals = keys.split("~");
            LOGGER.info("Starting Defragmentation on DB:" +vals[1]+ " On Server:" + vals[0]);
            ConnectionManager cm;
            int tableCount =0;
            try{
                cm = new ConnectionManager(vals[0],vals[1],"sa","IDeaS123");
                try {
                    ResultSet rs = cm.getRecords("SELECT dbschemas.[name] as 'Schema',dbtables.[name] as 'Table',dbindexes.[name] as 'Index',indexstats.avg_fragmentation_in_percent,indexstats.page_count FROM sys.dm_db_index_physical_stats (DB_ID(), NULL, NULL, NULL, null) AS indexstats INNER JOIN sys.tables dbtables on dbtables.[object_id] = indexstats.[object_id] INNER JOIN sys.schemas dbschemas on dbtables.[schema_id] = dbschemas.[schema_id] INNER JOIN sys.indexes AS dbindexes ON dbindexes.[object_id] = indexstats.[object_id] AND indexstats.index_id = dbindexes.index_id WHERE indexstats.database_id = DB_ID() ORDER BY indexstats.avg_fragmentation_in_percent desc");
                    tableCount = 0;
                    while (rs.next()) {
                        if (rs.getString("Index") != null && Double.parseDouble(rs.getString("avg_fragmentation_in_percent")) > 30) {
                            cm.Execute("alter index " + rs.getString("Index") + " on " + rs.getString("Schema") + "." + rs.getString("Table") + " REBUILD");
                           // LOGGER.info("for catlog:" + vals[1] + " tabel:" + rs.getString("Table") + " index:" + rs.getString("Index") + "fragmentation" + rs.getString("avg_fragmentation_in_percent") );
                            tableCount++;
                        } else {
                            if (rs.getString("Index") != null && Double.parseDouble(rs.getString("avg_fragmentation_in_percent")) > 2) {
                                cm.Execute("alter index " + rs.getString("Index") + " on " + rs.getString("Schema") + "." + rs.getString("Table") + " REORGANIZE");
                               // LOGGER.debug("for catlog:" + vals[1] + " tabel:" + rs.getString("Table") + " index:" + rs.getString("Index") + "fragmentation" + rs.getString("avg_fragmentation_in_percent"));
                                tableCount++;
                            }
                        }
                    }
                    rs.close();
                }
                catch (Exception e){
                    LOGGER.error(e);
                }
                cm.dropConnection();
            }catch (Exception e){
                LOGGER.error("Error while connecting Server:" + vals[0] + " DB:"+vals[1],e);
            }
            LOGGER.info("Completed Defragmentation on DB:" +vals[1]+ " On Server:" + vals[0] + " total tables: "+tableCount);
        }
    }

}



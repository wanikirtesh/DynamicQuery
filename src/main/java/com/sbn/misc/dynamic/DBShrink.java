package com.sbn.misc.dynamic;

import com.sbn.utills.ConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;

public class DBShrink {
    private static Logger LOGGER = LogManager.getLogger(DBShrink.class);

    public static void main(String[] args) throws Exception {
        String lstFilesSQL = "SELECT d.name DatabaseName, f.name LogicalName FROM sys.master_files f INNER JOIN sys.databases d ON d.database_id = f.database_id where d.name not in ('Master','msdb','model') and d.name like '231%'";
        String[] serverNames = {"mn4sg3xdbsw102.ideasstg.int","mn4sg3xdbsw104.ideasstg.int","mn4sg3xdbsw105.ideasstg.int"};
        for (String serverName : serverNames) {
            ConnectionManager cm = new ConnectionManager(serverName, "master", "sa", "IDeaS123");
            ResultSet dbFiles = cm.getRecords(lstFilesSQL);
            while (dbFiles.next()){
                try {
                    LOGGER.info("Shrinking file: " + dbFiles.getString("LogicalName") + " for db: " + dbFiles.getString("DatabaseName") + " On server: "+serverName);
                    ConnectionManager cTemp = new ConnectionManager(serverName, dbFiles.getString("DatabaseName"), "sa", "IDeaS123");
                    cTemp.Execute("DBCC SHRINKFILE(N'" + dbFiles.getString("LogicalName") + "',0,TRUNCATEONLY);");
                    cTemp.dropConnection();
                }catch (Exception e){
                    LOGGER.error(e);
                }
            }
        }
    }
}

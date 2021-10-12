package com.sbn.misc.dynamic;

import com.sbn.utills.ConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;

public class RunOnEachDB {
    static Logger LOGGER = LogManager.getLogger();
    public static void main(String[] args) throws Exception {
        String lstFilesSQL ="SELECT name, database_id, create_date FROM sys.databases;";
        //String[] serverNames = {"mn4sg3xdbsw102.ideasstg.int","mn4sg3xdbsw103.ideasstg.int","mn4sg3xdbsw104.ideasstg.int","mn4sg3xdbsw105.ideasstg.int"};
        String[] serverNames = {"mn4sg3xdbsw103.ideasstg.int"};
        for (String serverName : serverNames) {
            ConnectionManager cm = new ConnectionManager(serverName, "master", "sa", "IDeaS123");
            ResultSet dbFiles = cm.getRecords(lstFilesSQL);
            String fileName = serverName+".csv";
            if(Files.exists(Paths.get(fileName))){
               Files.delete(Paths.get(fileName));
            }
            Files.createFile(Paths.get(fileName));
            while (dbFiles.next()){
                try{

                    ResultSet records = cm.getRecords("select DB_NAME() as name, count(*) as count  from ["+dbFiles.getString("name")+"].dbo.Pace_Group_Block");
                    records.next();
                    String contentToAppend = dbFiles.getString("name")+","+records.getString("count") + "\r\n";
                    Files.write(
                            Paths.get(fileName),
                            contentToAppend.getBytes(),
                            StandardOpenOption.APPEND);
                    records.close();

                }catch (Exception e){
                    LOGGER.error(e);
                }
            }
            dbFiles.close();
            cm.dropConnection();

        }
    }
}

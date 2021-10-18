package com.sbn.misc.dynamic;

import com.sbn.utills.ConnectionManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.List;

public class LinkedSrpGlobalInsert {
    public static void main(String[] args) throws Exception {
        //globalSRP();
        activeSRP();
    }

    private static void globalSRP() throws Exception {
        ConnectionManager cm = new ConnectionManager("mn4sg3xdbsw101.ideasstg.int", "master", "sa", "IDeaS123");
        List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\idnkiw\\OneDrive - SAS\\Desktop\\HCRS_SRP_Derivation_20210427.psv"));
        String strQuery = "Insert InTo [Ratchet].[dbo].Client_Linked_SRP_Mappings (Property_Code,SRP_Code,Linked_Srp_Code,Offset_Type,Yield_As,Created_DTTM,External_System) values (?,?,?,?,?,CURRENT_TIMESTAMP,'HCRS')";
        for(int pid=22001;pid<=22001;pid++) {
            String delQuery = "Delete from [Ratchet].[dbo].Client_Linked_SRP_Mappings where Property_Code = '"+pid+"'";
            cm.Execute(delQuery);
            PreparedStatement myStatements = cm.getPreparedStatementForQuery(strQuery);
            int i=0;
            for (String line : lines) {
                String[] cols = line.split("\\|");
                if (i != 0) {
                    myStatements.setString(1, ""+pid);
                    for(int j=1;j<=4;j++) {
                        myStatements.setString(j+1, cols[j]);
                    }
                    myStatements.addBatch();
                }
                i++;

            }
            myStatements.executeBatch();
            myStatements.close();
        }
    }

    private static void activeSRP() throws Exception {
        ConnectionManager cm = new ConnectionManager("mn4sg3xdbsw101.ideasstg.int", "master", "sa", "IDeaS123");
        List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\idnkiw\\OneDrive - SAS\\Desktop\\HCRS_SRP_Derivation_20210427.psv"));
        String strQuery = "Insert InTo [Ratchet].[dbo].Client_Active_Srps (Property_Code,SRP_Code,Linked_Srp_Code,Start_Date,End_Date,Created_DTTM,External_System) values (?,?,?,?,?,CURRENT_TIMESTAMP,'HCRS')";
        for(int pid=22001;pid<=22001;pid++) {
            String delQuery = "Delete from [Ratchet].[dbo].Client_Linked_SRP_Mappings where Property_Code = '"+pid+"'";
            cm.Execute(delQuery);
            PreparedStatement myStatements = cm.getPreparedStatementForQuery(strQuery);
            int i=0;
            for (String line : lines) {
                String[] cols = line.split("\\|");
                if (i != 0) {
                    myStatements.setString(1, ""+pid);
                    for(int j=1;j<=4;j++) {
                        myStatements.setString(j+1, cols[j]);
                    }
                    myStatements.addBatch();
                }
                i++;

            }
            myStatements.executeBatch();
            myStatements.close();
        }
    }

}
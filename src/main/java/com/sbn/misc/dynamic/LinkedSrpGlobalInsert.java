package com.sbn.misc.dynamic;

import com.sbn.utills.ConnectionManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LinkedSrpGlobalInsert {
    public static void main(String[] args) throws Exception {
        //Object[][] data = {{"SEAHM",20201,20210,"PCRS",""},{"SEAHM",20211,20220,"PCRS",""},{"SEAHM",20221,20240,"PCRS",""}};
        Object[][] data = { { "CHIDN", 20251, 20260, "HCRS", "20220107" },{ "CHIDN", "CHIDN", "HCRS", "20220107" } };
        for (Object[] datum : data) {
            if(data[1].getClass().isInstance(String.class)){
                derivationSRP(datum[0].toString(), datum[1].toString(), datum[3].toString(), datum[4].toString());
                activeSRP(datum[0].toString(), datum[1].toString(), datum[3].toString(), datum[4].toString());
            }else {
                derivationSRP(datum[0].toString(), (int) datum[1], (int) datum[2], datum[3].toString(), datum[4].toString());
                activeSRP(datum[0].toString(), (int) datum[1], (int) datum[2], datum[3].toString(), datum[4].toString());
            }
        }

    }

    private static void derivationSRP(String prodPropertyCode, int startProperty, int endProperty, String integration, String fileDTTM) throws Exception {
        ConnectionManager cm = new ConnectionManager("mn4sg3xdbsw101.ideasstg.int", "master", "sa", "IDeaS123");
        List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\idnkiw\\Downloads\\"+integration+"_SRP_Derivation_"+fileDTTM+".psv"));
        for(int pid=startProperty;pid<=endProperty;pid++) {
            insertDerivedSRPForProperty(prodPropertyCode, cm, lines, pid+"",integration);
        }
    }

    private static void derivationSRP(String prodPropertyCode, String startProperty, String integration, String fileDTTM) throws Exception {
        ConnectionManager cm = new ConnectionManager("mn4sg3xdbsw101.ideasstg.int", "master", "sa", "IDeaS123");
        List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\idnkiw\\Downloads\\"+integration+"_SRP_Derivation_"+fileDTTM+".psv"));
        insertDerivedSRPForProperty(prodPropertyCode, cm, lines, startProperty, integration);
    }

    private static void insertDerivedSRPForProperty(String prodPropertyCode, ConnectionManager cm, List<String> lines, String pid, String integration) throws SQLException {
        String strQuery = "Insert InTo [Ratchet].[dbo].Client_Linked_SRP_Mappings (Property_Code,SRP_Code,Linked_Srp_Code,Offset_Type,Yield_As,Created_DTTM,External_System) values (?,?,?,?,?,CURRENT_TIMESTAMP,'"+integration+"')";
        String delQuery = "Delete from [Ratchet].[dbo].Client_Linked_SRP_Mappings where Property_Code = '"+ pid +"'";
        cm.Execute(delQuery);
        PreparedStatement myStatements = cm.getPreparedStatementForQuery(strQuery);
        int i=0;
        for (String line : lines) {
            String[] cols = line.split("\\|");
            if (i != 0 && cols[0].equalsIgnoreCase(prodPropertyCode)) {
                myStatements.setString(1, ""+ pid);
                for(int j=1;j<=4;j++) {
                    myStatements.setString(j+1, cols[j]);
                }
                myStatements.addBatch();
            }
            i++;
        }
        System.out.println("executing batch...");
        myStatements.executeBatch();
        myStatements.close();
    }

    private static void activeSRP(String prodPropertyCode, int startProperty, int endProperty, String integration, String fileDTTM) throws Exception {
        ConnectionManager cm = new ConnectionManager("mn4sg3xdbsw101.ideasstg.int", "master", "sa", "IDeaS123");
        List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\idnkiw\\Downloads\\"+integration+"_ACTIVE_SRP_"+fileDTTM+".psv"));
        String strQuery = "Insert InTo [Ratchet].[dbo].Client_Active_Srps (Property_Code,SRP_Code,Start_Date,End_Date,Created_DTTM,External_System) values (?,?,?,?,CURRENT_TIMESTAMP,'"+integration+"')";
        for(int pid=startProperty;pid<=endProperty;pid++) {
            insertActiveSRPForProperty(prodPropertyCode, cm, lines, strQuery, pid+"");
        }
    }

    private static void activeSRP(String prodPropertyCode, String startProperty,  String integration, String fileDTTM) throws Exception {
        ConnectionManager cm = new ConnectionManager("mn4sg3xdbsw101.ideasstg.int", "master", "sa", "IDeaS123");
        List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\idnkiw\\Downloads\\"+integration+"_ACTIVE_SRP_"+fileDTTM+".psv"));
        String strQuery = "Insert InTo [Ratchet].[dbo].Client_Active_Srps (Property_Code,SRP_Code,Start_Date,End_Date,Created_DTTM,External_System) values (?,?,?,?,CURRENT_TIMESTAMP,'"+integration+"')";
        insertActiveSRPForProperty(prodPropertyCode, cm, lines, strQuery, startProperty);
    }

    private static void insertActiveSRPForProperty(String prodPropertyCode, ConnectionManager cm, List<String> lines, String strQuery, String pid) throws SQLException {
        String delQuery = "Delete from [Ratchet].[dbo].Client_Active_Srps where Property_Code = '"+ pid +"'";
        cm.Execute(delQuery);
        PreparedStatement myStatements = cm.getPreparedStatementForQuery(strQuery);
        int i=0;
        for (String line : lines) {
            String[] cols = line.split("\\|");
            if (i != 0 && cols[0].equalsIgnoreCase(prodPropertyCode)) {
                myStatements.setString(1, ""+ pid);
                for(int j=1;j<=3;j++) {
                    myStatements.setString(j+1, cols[j]);
                }
                myStatements.addBatch();
            }
            i++;
        }
        System.out.println("executing batch...");
        myStatements.executeBatch();
        myStatements.close();
    }

}
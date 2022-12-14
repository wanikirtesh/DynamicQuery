package com.sbn.misc.dynamic;

import com.sbn.utills.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LinkedSrpTransferForVirtualProperty {

    public static void main(String[] args) throws Exception {
        for(int i=20501;i<=20520;i++) {
            String propCode = i+"";
            ConnectionManager conSource = new ConnectionManager("MN4SG3XENVW001.ideasstg.int", "master", "sa", "IDeaS123");
            ConnectionManager conDestination = new ConnectionManager("mn4sg3xdbsw101.ideasstg.int", "master", "sa", "IDeaS123");
            setLinkedSRP(propCode, conSource, conDestination);
            setActiveSRP(propCode, conSource, conDestination);
            conDestination.dropConnection();
            conSource.dropConnection();
            System.out.println("done with db: "+i);
        }

    }

    private static void setActiveSRP(String propCode, ConnectionManager conSource, ConnectionManager conDestination) throws Exception {
        String strQuery = "select CONCAT(property_code,'_',SRP_Code) as SRP_Code,\n" +
                "'"+ propCode +"' as property_code,Start_Date,End_Date,Created_DTTM,External_System from [ratchet].dbo.Client_Active_Srps where property_code in('FRAAP', 'FRAHA')";
        ResultSet rs = conSource.getRecords(strQuery);
        String strInsertQuery = "Insert InTo [Ratchet].[dbo].Client_Active_Srps (Property_Code,SRP_Code,Start_Date,End_Date,Created_DTTM,External_System) values (?,?,?,?,CURRENT_TIMESTAMP,'HCRS')";
        String delQuery = "Delete from [Ratchet].[dbo].Client_Active_Srps where Property_Code = '"+ propCode +"'";
        conDestination.Execute(delQuery);
        PreparedStatement myStatements = conDestination.getPreparedStatementForQuery(strInsertQuery);
        while (rs.next()){
            myStatements.setString(1, propCode);
            myStatements.setString(2,rs.getString("SRP_Code"));
            myStatements.setString(3,rs.getString("Start_Date"));
            myStatements.setString(4,rs.getString("End_Date"));
            myStatements.addBatch();
        }
        rs.close();
        myStatements.executeBatch();
    }

    private static void setLinkedSRP(String propCode, ConnectionManager conSource, ConnectionManager conDestination) throws Exception {
        String strQuery = "select CONCAT(property_code,'_',SRP_Code) as SRP_Code,\n" +
                "'"+ propCode +"' as property_code,\n" +
                "case When Linked_SRP_Code != 'LV0' then CONCAT(property_code,'_',Linked_SRP_Code)\n" +
                "else Linked_SRP_Code\n" +
                "end  Linked_SRP_Code,offset_type,Yield_As,Created_DTTM,External_System\n" +
                "from [ratchet].dbo.client_linked_srp_mappings where property_code in('FRAAP', 'FRAHA')\n";
        ResultSet rs = conSource.getRecords(strQuery);
        String strInsertQuery = "Insert InTo [Ratchet].[dbo].Client_Linked_SRP_Mappings (Property_Code,SRP_Code,Linked_Srp_Code,Offset_Type,Yield_As,Created_DTTM,External_System) values (?,?,?,?,?,CURRENT_TIMESTAMP,'HCRS')";
        String delQuery = "Delete from [Ratchet].[dbo].Client_Linked_SRP_Mappings where Property_Code = '"+ propCode +"'";
        conDestination.Execute(delQuery);
        PreparedStatement myStatements = conDestination.getPreparedStatementForQuery(strInsertQuery);
        while (rs.next()){
            myStatements.setString(1, propCode);
            myStatements.setString(2,rs.getString("SRP_Code"));
            myStatements.setString(3,rs.getString("Linked_Srp_Code"));
            myStatements.setString(4,rs.getString("Offset_Type"));
            myStatements.setString(5,rs.getString("Yield_As"));
            myStatements.addBatch();
        }
        rs.close();
        myStatements.executeBatch();
    }
}

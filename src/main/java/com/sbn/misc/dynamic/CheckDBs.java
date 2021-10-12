package com.sbn.misc.dynamic;

import com.sbn.utills.ConnectionManager;

import java.sql.ResultSet;

public class CheckDBs {
    public static void main(String[] args) throws Exception {
        String strQuery = "select server_name,dbname from [global].[dbo].[dbloc] d right join [global].[dbo].[Property] p on d.DBLoc_ID = p.DBLoc_ID where Client_ID = 15";
        ConnectionManager cm = new ConnectionManager("mn4sg3xdbsw101.ideasstg.int", "master", "sa", "IDeaS123");
        ResultSet properties = cm.getRecords(strQuery);
        while (properties.next()){
            ConnectionManager cTemp = null;
            try{
                cTemp = new ConnectionManager(properties.getString("server_name"),properties.getString("dbname"), "sa", "IDeaS123");
                System.out.println(properties.getString("server_name")+":"+properties.getString("dbname"));
            }catch (Exception e){
                System.out.println("#####"+properties.getString("server_name")+":"+properties.getString("dbname"));
            }finally {
                if(cTemp != null){
                    cTemp.dropConnection();
                }
            }

        }

        cm.dropConnection();

    }
}

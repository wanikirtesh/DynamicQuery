package com.sbn.misc.dynamic;

import com.sbn.utills.ConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class AnotherQueryRunner {
    private static Logger LOGGER = LogManager.getLogger(AnotherQueryRunner.class);
    public static void main(String[] args) throws Exception {
        ConnectionManager cm = new ConnectionManager("mn4sg3xdbsw009.ideasstg.int", "master", "sa", "IDeaS123");
        List<String> tables = Arrays.asList("PACE_Mkt_Occupancy_FCST","PACE_Accom_Activity","PACE_Accom_Occupancy_FCST","PACE_Mkt_Activity","PACE_Bar_Output","Mkt_Accom_Activity");
        for(int i=23130;i<=23134;i++) {
            for (String table : tables) {
                ResultSet rs = cm.getRecords("Select count(*) from ["+i+"].dbo."+table+" where property_id=10839 group by property_id;");
                if(rs.next()){
                    boolean x = cm.Execute("update ["+i+"].dbo."+table+" set property_id="+i+" where property_id<>1");
                }
                rs.close();
            }
            boolean result = cm.Execute("delete from ["+i+"].dbo.property where Property_ID=10839");
            ResultSet rs = cm.getRecords("select count(*) from ["+i+"].dbo.property");
            rs.next();
            if(rs.getInt(1)>2){
                LOGGER.error("######### Test Filed for "+ i + " Total property count is " + rs.getInt(1));
            }else {
                LOGGER.info("Test passed for "+ i + " Total property count is " + rs.getInt(1));
            }
            rs.close();
        }
        cm.dropConnection();

    }

}

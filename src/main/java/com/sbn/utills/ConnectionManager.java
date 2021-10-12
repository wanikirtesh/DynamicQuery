package com.sbn.utills;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;


public class ConnectionManager {
    public static int getNoOfConnection =0;
    private Connection conn=null;
    private List<Statement> statementList = new ArrayList<Statement>();
    private Logger LOGGER = LogManager.getLogger(ConnectionManager.class);
    public ConnectionManager() throws Exception
    {
        getConnection();

    }

    public ConnectionManager(String server, String db, String uname, String pass) throws Exception {
        getConnection(server,db,uname,pass);
    }

    public ResultSet getRecords(String strSQL) throws Exception
    {
        LOGGER.info("Fetching Data With Query" + strSQL);
        try
        {
            Statement s = conn.createStatement();
            statementList.add(s);
            s = conn.createStatement();
            //System.out.println(strSQL);
            return s.executeQuery(strSQL);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
    private void getConnection()throws Exception
    {
        LOGGER.debug("Creating new Database Connection");
        ResourceBundle dbDetails = ResourceBundle.getBundle("dbSource");
        String ConnStr=dbDetails.getString("dbServer");
        String uname=dbDetails.getString("UserName");
        String pass=dbDetails.getString("Password");
        String connectionURL = ConnStr;
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn= DriverManager.getConnection(connectionURL, uname, pass);
            getNoOfConnection++;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
    private void getConnection(String server,String db, String uname, String pass)throws Exception
    {
        LOGGER.debug("Creating new Database Connection");
        String connectionURL = "jdbc:sqlserver://"+server+"\\G3SQL01;databaseName="+db;
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn= DriverManager.getConnection(connectionURL, uname, pass);
            getNoOfConnection++;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }


    public void dropConnection()
    {

        LOGGER.debug("Dropping Database Connection");
        for (int i = 0; i < statementList.size(); i++) {
            try {
                statementList.get(i).close();
            } catch (SQLException e) {  LOGGER.error(e);
                //ignore
            }

        }

        try
        {
            if(!conn.isClosed())
            {

                getNoOfConnection--;
                conn.close();
                LOGGER.debug("Active DB Connections count is: " + getNoOfConnection);
            }
        }
        catch (Exception e) {  LOGGER.error(e);
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    public boolean Execute(String strSQL) throws SQLException {

        LOGGER.debug("Executing SQL Query on catlog:" + conn.getCatalog() + " query:" + strSQL);
        try {
            Statement st = conn.createStatement();
            LOGGER.info("return:" + st.execute(strSQL));
            return true;

        }catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
            return false;
        }

    }

    public PreparedStatement getPreparedStatementForQuery(String query) throws SQLException {
        return conn.prepareStatement(query);
    }
}

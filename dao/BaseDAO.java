package com.fousalert.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fousalert.utils.Constants;

public class BaseDAO {
	protected DatabaseManager databaseManager;
	
	public   BaseDAO(){
		databaseManager = new DatabaseManager();
	}
	public  boolean createDB() throws ClassNotFoundException, SQLException{
		Connection connection =null;
		PreparedStatement preparedStatement = null;
		int count = 0 ;
		Boolean status = false;
		connection = databaseManager.getConnection();
		if(connection==null)
		{return false;}
		connection.setAutoCommit(false);
		String sql1= "CREATE TABLE "+Constants.applicationProperties.getProperty("db.user.table.name")+ "("+
				"UserId	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
				"UserName	TEXT NOT NULL,"+
				"LayoutId INTEGER,"+
				"CreatedBy	INTEGER,"+
				"CreationDateTime	TEXT,"+
				"ModifiedBy	TEXT,"+
				"ModifiedTime	TEXT,"+
				"FOREIGN KEY(LayoutId) REFERENCES "+Constants.applicationProperties.getProperty("db.application.layout.table.name")+"(LayoutId))";
				;
		String sql2="CREATE TABLE "+Constants.applicationProperties.getProperty("db.user.details.table.name")+" ("+
				"UserDetailsId	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"+
				"UserId	INTEGER,"+
				"FirstName	TEXT,"+
				"LastName	TEXT,"+
				"Address	TEXT,"+
				"ModifiedBy	TEXT,"+
				"ModifiedTime	TEXT,"+
				"FOREIGN KEY(UserId) REFERENCES "+Constants.applicationProperties.getProperty("db.user.table.name")+"(UserId))";
		String sql3="CREATE TABLE "+Constants.applicationProperties.getProperty("db.subscribed.ticker.table.name")+" ("+
				"TickerSymbol	TEXT NOT NULL,"+
				"UserId	INTEGER NOT NULL,"+
				"PRIMARY KEY(TickerSymbol,UserId))";
		String sql4="CREATE TABLE "+Constants.applicationProperties.getProperty("db.user.preferences.table.name")+" ("+
				"PreferencesId	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"+
				"UserId	INTEGER,"+
				"ChartPreferences	TEXT,"+
				" FOREIGN KEY(UserId) REFERENCES "+Constants.applicationProperties.getProperty("db.user.table.name")+"(UserId))";
		String sql5="CREATE TABLE "+Constants.applicationProperties.getProperty("db.master.ticker.table.name")+" ("+
				"TickerSymbol STRING NOT NULL PRIMARY KEY, "
				+ "UserId INTEGER NOT NULL)";
		String sql6="CREATE TABLE "+Constants.applicationProperties.getProperty("db.application.layout.table.name")+" ("+
				    "LayoutId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"+
				    "LayoutName TEXT,"+
				    "UserId INTEGER ,"+
				"layoutJSON TEXT ,"+	
				    "FOREIGN KEY(UserId) REFERENCES "+Constants.applicationProperties.getProperty("db.user.table.name")+"(UserId))";
		preparedStatement = connection.prepareStatement(sql1);
		try{
			if(preparedStatement==null)
			{
				connection.close();
				return false;
			}
			if(preparedStatement.executeUpdate()==0) {
				count++;
			}

			preparedStatement = null;
			preparedStatement = connection.prepareStatement(sql2);
			if(preparedStatement==null)
			{
				connection.close();
				return false;
			}
			if(preparedStatement.executeUpdate()==0) {
				count++;
			}

			preparedStatement = null;
			preparedStatement = connection.prepareStatement(sql3);
			if(preparedStatement==null) {
				connection.close();
				return false;
			}
			if(preparedStatement.executeUpdate()==0) {
				count++;
			}

			preparedStatement = null;
			preparedStatement = connection.prepareStatement(sql4);
			if(preparedStatement==null) {
				connection.close();
				return false;
			}
			if(preparedStatement.executeUpdate()==0) {
				count++;
			}

			preparedStatement = null;
			preparedStatement = connection.prepareStatement(sql5);
			if(preparedStatement==null) {
				connection.close();
				return false;
			}
			if(preparedStatement.executeUpdate()==0) {
				count++;
			}
			preparedStatement = null;
			preparedStatement = connection.prepareStatement(sql6);
			if(preparedStatement==null) {
				connection.close();
				return false;
			}
			if(preparedStatement.executeUpdate()==0) {
				count++;
			}
			
			System.out.println(count);
			if(count == 6) {
				status = true; 
				connection.commit();
			}
			else {
				status = false; 
				connection.rollback();
			}
			closeResources(connection, preparedStatement);
			return status;
		}
		catch(SQLException exception) {
			connection.rollback();
			closeResources(connection, preparedStatement);
			throw new SQLException(exception);
		}
	}
	protected void closeResources(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet) throws SQLException
	{    if(preparedStatement != null)
		      preparedStatement.close();
	      if(resultSet != null )
	    	  resultSet.close();
		  if(connection != null)
	          connection.close();
	}
	protected void closeResources(Connection connection,PreparedStatement preparedStatement) throws SQLException
	{     if(preparedStatement != null)
		  			preparedStatement.close();
		  if(connection != null)
			  		connection.close();
	}
	protected List<String> readingResultSet(ResultSet resultSet) throws SQLException
	{
		List<String> subscribedTickers = new ArrayList<String>();
		
		while(resultSet.next())
		{
			subscribedTickers.add(resultSet.getString("TickerSymbol"));
			
		}
		return subscribedTickers;
	}
}

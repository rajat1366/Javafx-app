package com.fousalert.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.fousalert.utils.Constants;

public class DatabaseManager {

	public Connection getConnection() throws ClassNotFoundException, SQLException{
		  Connection connection= null  ;
		  Class.forName(Constants.applicationProperties.getProperty("db.driver.class"));
	      connection = DriverManager.getConnection(Constants.applicationProperties.getProperty("db.url")+
	    		                                   Constants.applicationProperties.getProperty("db.fileName").replace("{version}", "v1.22"));
		  return connection;
	}
}

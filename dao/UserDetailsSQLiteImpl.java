package com.fousalert.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDetailsSQLiteImpl {

	private DatabaseManager databaseManager;
	public   UserDetailsSQLiteImpl(){
		databaseManager = new DatabaseManager();
	}
	public Integer getUserId(String userName) throws ClassNotFoundException, SQLException
	{    ResultSet resultSet = null;
	 Integer userId ;	
		 Connection connection = databaseManager.getConnection();
		 String sql = "SELECT UserID FROM FousAlertUser WHERE UserName =?";
		 PreparedStatement preparedStatement = connection.prepareStatement(sql);
		 preparedStatement.setString(1, userName);
		 resultSet = preparedStatement.executeQuery();
		 if(resultSet.next()){
		  userId = resultSet.getInt("UserId");
		
		 }else
		 {
			 userId=null;
		 }
		 closeResources(connection,preparedStatement,resultSet);
		 return userId;
	}
	public String getChartPreferences(Integer userId) throws SQLException, ClassNotFoundException
	{
		Connection connection = databaseManager.getConnection();
		String sql = "SELECT ChartPreferences FROM UserPreferences WHERE UserId=?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setInt(1, userId);
		ResultSet resultSet = preparedStatement.executeQuery();
		resultSet.next();
		String chartPreferences = resultSet.getString("ChartPreferences");
		closeResources(connection, preparedStatement, resultSet);
		return chartPreferences;
	}
	public void setChartPreferences(Integer userId,String chartPreferences) throws ClassNotFoundException, SQLException
	{
		Connection connection = databaseManager.getConnection();
		String sql = "UPDATE UserPreferences set ChartPreferences = ? WHERE UserID = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, chartPreferences);
		preparedStatement.setInt(2, userId);
		preparedStatement.executeUpdate();
	    closeResources(connection, preparedStatement);
	}
	public String getSubscribedTickers(Integer userId) throws SQLException, ClassNotFoundException
	{
		Connection connection = databaseManager.getConnection();
		String sql = "SELECT TickersSubscribed FROM UserPreferences WHERE UserId=?";
		PreparedStatement preparedStatement  = connection.prepareStatement(sql);
		preparedStatement.setInt(1, userId);
		ResultSet resultSet = preparedStatement.executeQuery();
		resultSet.next();
		String tickersSubscribed = resultSet.getString("TickersSubscribed");
		closeResources(connection, preparedStatement, resultSet);
		return tickersSubscribed;
	}
	public void closeResources(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet) throws SQLException
	{     
		  preparedStatement.close();
	      resultSet.close();
		  connection.close();
	}
	public void closeResources(Connection connection,PreparedStatement preparedStatement) throws SQLException
	{     
		  preparedStatement.close();
		  connection.close();
	}
}

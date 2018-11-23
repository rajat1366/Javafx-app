package com.fousalert.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fousalert.bean.Layout;
import com.fousalert.utils.Constants;
import com.mysql.jdbc.Statement;

public class UserDAO extends BaseDAO {

	public Integer getUserId(String userName) throws ClassNotFoundException, SQLException {
		ResultSet resultSet = null;
		Integer userId =-1;	
		Connection connection = databaseManager.getConnection();
		String sql = "SELECT UserID FROM "+Constants.applicationProperties.getProperty("db.user.table.name")+" WHERE UserName =?";
		if(connection==null) {
			return null;
		}

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		if(preparedStatement==null) {
			connection.close();
			return null;
		}
		preparedStatement.setString(1, userName);
		resultSet = preparedStatement.executeQuery();
		if(resultSet.next()) {
			userId = resultSet.getInt("UserId");

		}
		closeResources(connection,preparedStatement,resultSet);
		return userId;
	}

	public boolean setUserLayoutId(Integer userId, Integer layoutId) throws ClassNotFoundException, SQLException {   
		PreparedStatement preparedStatement = null;
		Connection connection =null;
		int i=0;
		connection = databaseManager.getConnection();
		String sql = "UPDATE "+Constants.applicationProperties.getProperty("db.user.table.name")+" set LayoutId = ? WHERE UserID = ?";
		if(connection==null) {
			return false;
		}
		preparedStatement = connection.prepareStatement(sql);
		if(preparedStatement==null) {
			connection.close();
			return false;
		}
		preparedStatement.setInt(1, layoutId);
		preparedStatement.setInt(2, userId);
		i=preparedStatement.executeUpdate();
		closeResources(connection, preparedStatement);
		if(i > 0) {
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean updateLayoutJson(Integer layoutId, String layoutJson) throws ClassNotFoundException, SQLException {   
		PreparedStatement preparedStatement = null;
		Connection connection =null;
		int i=0;
		connection = databaseManager.getConnection();
		String sql = "UPDATE "+Constants.applicationProperties.getProperty("db.application.layout.table.name")+" set layoutJSON = ? WHERE LayoutId = ?";
		if(connection==null) {
			return false;
		}
		preparedStatement = connection.prepareStatement(sql);
		if(preparedStatement==null) {
			connection.close();
			return false;
		}
		preparedStatement.setString(1, layoutJson);
		preparedStatement.setInt(2, layoutId);
		i=preparedStatement.executeUpdate();
		closeResources(connection, preparedStatement);
		if(i > 0) {
			return true;
		}
		else{
			return false;
		}
	}

	public Integer addNewLayout(Integer userId, String layoutJson, String layoutName) {
		try{
			PreparedStatement preparedStatement =null;
			ResultSet resultSet = null;
			Connection connection = null;
			Integer layoutID = 0 ;
			int i=0;
			connection= databaseManager.getConnection();
			if(connection==null) {
				return null;
			}
			String sql= "INSERT INTO "+Constants.applicationProperties.getProperty("db.application.layout.table.name") +" "
					+ "(UserId, layoutJSON, LayoutName) VALUES(?,?, ?)";
			preparedStatement = null;
			preparedStatement  = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			if(preparedStatement == null)  {
				connection.close();
				return null;
			}
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, layoutJson);
			preparedStatement.setString(3, layoutName);
			i = preparedStatement.executeUpdate();
			resultSet = preparedStatement.getGeneratedKeys();
			if(resultSet != null) {
				if(resultSet.next()) {
					layoutID  = resultSet.getInt(1);
				}
			}
			closeResources(connection, preparedStatement);
			if(i > 0) {
				return layoutID;
			} else {
				return null;
			}
		}catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}

	}
	public Boolean createChartPreferencesByUserId(Integer userId,String chartPreferences) {
		try{
			PreparedStatement preparedStatement =null;
			Connection connection = null;
			int i=0;
			connection= databaseManager.getConnection();
			if(connection == null) {
				return null;
			} else {

				String saveChartPreferencesQuery = "INSERT INTO "+Constants.applicationProperties.getProperty("db.user.preferences.table.name") +" "
						+ "(UserId, ChartPreferences) VALUES"
						+ "(?,?)";
				preparedStatement = null;
				preparedStatement  = connection.prepareStatement(saveChartPreferencesQuery);
				if(preparedStatement == null)  {
					connection.close();
					return null;
				}
				preparedStatement.setInt(1, userId);
				preparedStatement.setString(2, chartPreferences);
				i = preparedStatement.executeUpdate();

				closeResources(connection, preparedStatement);
				if(i > 0) {
					return true;
				} else {
					return null;
				}
			}
		}catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public String getChartPreferences(Integer userId) throws SQLException, ClassNotFoundException {   
		String chartPreferences =null;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet= null;

		connection = databaseManager.getConnection();
		String sql = "SELECT ChartPreferences FROM "+Constants.applicationProperties.getProperty("db.user.preferences.table.name")+" WHERE UserId=?";
		if(connection == null) {
			return chartPreferences;
		}
		preparedStatement = connection.prepareStatement(sql);
		if(preparedStatement==null)	{
			connection.close();
			return chartPreferences;
		}
		preparedStatement.setInt(1, userId);
		resultSet = preparedStatement.executeQuery();
		if(resultSet == null)	{
			closeResources(connection, preparedStatement);
			return chartPreferences;
		}
		resultSet.next();
		chartPreferences = resultSet.getString("ChartPreferences");
		closeResources(connection, preparedStatement, resultSet);

		return chartPreferences;
	}


	public boolean setChartPreferences(Integer userId,String chartPreferences) throws ClassNotFoundException, SQLException	{   
		PreparedStatement preparedStatement = null;
		Connection connection =null;
		int i=0;
		connection = databaseManager.getConnection();
		String sql = "UPDATE "+Constants.applicationProperties.getProperty("db.user.preferences.table.name")+" set ChartPreferences = ? WHERE UserID = ?";
		if(connection==null) {
			return false;
		}

		preparedStatement = connection.prepareStatement(sql);
		if(preparedStatement==null)	{
			connection.close();
			return false;
		}
		preparedStatement.setString(1, chartPreferences);
		preparedStatement.setInt(2, userId);
		i=preparedStatement.executeUpdate();
		closeResources(connection, preparedStatement);
		if(i > 0) {
			return true;
		}
		else{
			return false;
		}

	}

	public List<String> getSubscribedTickers(Integer userId) throws SQLException, ClassNotFoundException	{
		Connection connection =null;
		PreparedStatement preparedStatement =null;
		ResultSet resultSet = null;
		List<String> subscribedTickers =null;

		connection = databaseManager.getConnection();
		String sql = "SELECT TickerSymbol FROM "+Constants.applicationProperties.getProperty("db.subscribed.ticker.table.name")+"  WHERE UserId=?";
		if(connection==null)		{
			return subscribedTickers;
		}

		preparedStatement  = connection.prepareStatement(sql);
		if(preparedStatement==null)		{
			connection.close();
			return subscribedTickers;
		}
		preparedStatement.setInt(1, userId);
		resultSet = preparedStatement.executeQuery();
		if(resultSet == null)		{
			closeResources(connection, preparedStatement);
			return subscribedTickers;
		}
		subscribedTickers = readingResultSet(resultSet);
		closeResources(connection, preparedStatement, resultSet);
		return subscribedTickers;
	}


	public boolean addNewTicker(String tickerSymbol,Integer userId) throws SQLException, ClassNotFoundException	{ 
		PreparedStatement preparedStatement =null;
		ResultSet resultSet = null;
		Connection connection = null;
		int i=0;
		connection= databaseManager.getConnection();
		if(connection==null) {
			return false;
		}
		String checkRecordExists = "SELECT TickerSymbol FROM "+Constants.applicationProperties.getProperty("db.subscribed.ticker.table.name")+
				" where TickerSymbol=? AND UserId =?;";
		String sql = "INSERT INTO  "+Constants.applicationProperties.getProperty("db.subscribed.ticker.table.name")+" (TickerSymbol,UserId) VALUES (?,?)";

		preparedStatement = connection.prepareStatement(checkRecordExists);
		if(preparedStatement == null) 	{
			connection.close();
			return false;
		}
		preparedStatement.setString(1, tickerSymbol);
		preparedStatement.setInt(2, userId);

		resultSet = preparedStatement.executeQuery();
		if(resultSet.next()) {
			closeResources(connection, preparedStatement, resultSet);
			return true;
		}

		preparedStatement = null;
		preparedStatement  = connection.prepareStatement(sql);
		if(preparedStatement == null) 	{
			connection.close();
			return false;
		}
		preparedStatement.setString(1, tickerSymbol);
		preparedStatement.setInt(2, userId);
		i = preparedStatement.executeUpdate();
		closeResources(connection, preparedStatement);
		if(i > 0)	{
			return true;
		}
		else {
			return false;
		}
	}

	public boolean removeTicker(String tickerSymbol,Integer userId) throws ClassNotFoundException, SQLException	{
		Connection connection = null;
		int count=0;
		String sql = "DELETE from "+Constants.applicationProperties.getProperty("db.subscribed.ticker.table.name")+
				" where TickerSymbol=? AND UserId =?;";
		String checkRecordExists = "SELECT TickerSymbol FROM "+Constants.applicationProperties.getProperty("db.subscribed.ticker.table.name")+
				" where TickerSymbol=? AND UserId =?;";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		connection = databaseManager.getConnection();
		if(connection==null) {
			return false;
		}
		preparedStatement = connection.prepareStatement(checkRecordExists);
		preparedStatement.setString(1, tickerSymbol);
		preparedStatement.setInt(2, userId);

		resultSet = preparedStatement.executeQuery();
		if(!resultSet.next()) {
			closeResources(connection, preparedStatement, resultSet);
			return true;
		}

		preparedStatement = null;
		preparedStatement = connection.prepareStatement(sql);
		if(preparedStatement == null) {
			connection.close();
			return false;
		}
		preparedStatement.setString(1, tickerSymbol);
		preparedStatement.setInt(2, userId);
		count = preparedStatement.executeUpdate();
		closeResources(connection, preparedStatement,resultSet);
		if(count > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean createUserAndDetails(Integer userId,String userName) throws ClassNotFoundException, SQLException	{   
		Connection connection = null;
		Boolean status = false;
		PreparedStatement preparedStatement= null;
		int i=0;
		String sql1= "INSERT INTO "+Constants.applicationProperties.getProperty("db.user.table.name") +" "
				+ "(UserId, UserName, LayoutId, CreatedBy, CreationDateTime,ModifiedBy,ModifiedTime) VALUES"
				+ "(?,?,?,?,?,?,?)";

		String sql2= "INSERT INTO "+Constants.applicationProperties.getProperty("db.user.preferences.table.name") +" "
				+ "(UserId, ChartPreferences) VALUES"
				+ "(?,?)";

		String sql3 ="INSERT INTO "+Constants.applicationProperties.getProperty("db.application.layout.table.name") +
				"(LayoutName,UserId, layoutJSON) VALUES(?,?,?)";

		connection =  databaseManager.getConnection();
		if(connection == null)		{
			return false;
		}
		connection.setAutoCommit(false);
		preparedStatement=connection.prepareStatement(sql1);
		if(preparedStatement == null) {
			connection.close();
			return false;
		}
		preparedStatement.setInt(1, userId);
		preparedStatement.setString(2, userName);
		preparedStatement.setInt(3, 1);
		preparedStatement.setString(4, "");
		preparedStatement.setString(5, "");
		preparedStatement.setString(6, "");
		preparedStatement.setString(7, "");

		i = i + preparedStatement.executeUpdate();
		try{
			preparedStatement = null;
			preparedStatement=connection.prepareStatement(sql2);
			if(preparedStatement == null) {   
				connection.rollback();
				connection.close();
				return false;
			}
			
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2,Constants.applicationProperties.getProperty("chart.ui.default.properties"));
			i=i+preparedStatement.executeUpdate();

			preparedStatement = null;
			preparedStatement=connection.prepareStatement(sql3);
			if(preparedStatement == null) {   
				connection.rollback();
				connection.close();
				return false;
			}
			
			preparedStatement.setString(1,"Default Layout" );
			preparedStatement.setInt(2, userId);
			preparedStatement.setString(3, Constants.applicationProperties.getProperty("layout.default.properties"));
			i=i+preparedStatement.executeUpdate();

			if(i == 3) {
				status = true; 
				connection.commit();
			} else {
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
	
	public Integer saveUILayoutString(Long userId, String layoutString) throws ClassNotFoundException, SQLException {
		String layoutSaveQuery ="INSERT INTO " + Constants.applicationProperties.getProperty("db.application.layout.table.name") + "(LayoutId,LayoutName,UserId, layoutJSON) VALUES (?,?,?,?)";
		
		Connection connection =  databaseManager.getConnection();
		PreparedStatement preparedStatement = null;
		if(connection != null) {
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(layoutSaveQuery);
			preparedStatement.setInt(1, 1);
			preparedStatement.setString(2,"Default Layout" );
			preparedStatement.setLong(3, userId);
			preparedStatement.setString(4, layoutString);
			Integer layoutId = preparedStatement.executeUpdate();
			connection.commit();
			closeResources(connection, preparedStatement);
			return layoutId;
		} else {
			return null;
		}
	}
	
	public void setLayoutIdForUserId(Integer userId, Integer layoutId) throws ClassNotFoundException, SQLException {
		String layoutSaveQuery ="ALTER TABLE" + Constants.applicationProperties.getProperty("db.application.layout.table.name") + " SET LayoutId=? WHERE UserId=?";
		
		Connection connection =  databaseManager.getConnection();
		PreparedStatement preparedStatement = null;
		if(connection != null) {
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(layoutSaveQuery);
			preparedStatement.setInt(1, layoutId);
			preparedStatement.setInt(2, userId);
			preparedStatement.executeUpdate();
			connection.commit();
			closeResources(connection, preparedStatement);
		}
	}

	public boolean insertMasterTickerData(String tickerSymbol,Integer userId) throws SQLException, ClassNotFoundException	{ 
		PreparedStatement preparedStatement =null;
		Connection connection = null;
		int i=0;
		connection= databaseManager.getConnection();
		if(connection==null) {
			return false;
		}
		String sql = "INSERT INTO "+Constants.applicationProperties.getProperty("db.master.ticker.table.name")+" (TickerSymbol,UserId) VALUES (?,?)";
		preparedStatement  = connection.prepareStatement(sql);
		if(preparedStatement == null) 	{
			connection.close();
			return false;
		}
		preparedStatement.setString(1, tickerSymbol);
		preparedStatement.setInt(2, userId);
		i = preparedStatement.executeUpdate();
		closeResources(connection, preparedStatement);
		if(i > 0) {
			return true;
		} else {
			return false;
		}
	}

	public List<String> fetchMasterTickerByUserId(Integer userId) throws ClassNotFoundException, SQLException {
		ResultSet resultSet = null;	
		Connection connection = databaseManager.getConnection();
		String sql = "SELECT TickerSymbol FROM "+Constants.applicationProperties.getProperty("db.master.ticker.table.name")+" WHERE UserId =?";
		if(connection==null) {
			return null;
		}

		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		if(preparedStatement==null) {
			connection.close();
			return null;
		}
		preparedStatement.setInt(1, userId);
		resultSet = preparedStatement.executeQuery();
		List<String> tickerList = new ArrayList<String>();
		if(resultSet.next()) {
			tickerList.add(resultSet.getString("TickerSymbol"));
		}
		closeResources(connection,preparedStatement,resultSet);
		return tickerList;

	}
	
	public String getLayoutJsonByLayoutId(Integer layoutId) throws SQLException, ClassNotFoundException {
		Connection connection =null;
		PreparedStatement preparedStatement =null;
		ResultSet resultSet = null;
		String layoutJson = null;

		connection = databaseManager.getConnection();
		String sql = "SELECT layoutJSON FROM "+Constants.applicationProperties.getProperty("db.application.layout.table.name")+"  WHERE LayoutId=?";
		if(connection==null)  {
			return layoutJson;
		}
		preparedStatement  = connection.prepareStatement(sql);
		if(preparedStatement==null)  {
			connection.close();
			return layoutJson;
		}
		preparedStatement.setInt(1, layoutId);

		resultSet = preparedStatement.executeQuery();
		if(resultSet == null)  {
			closeResources(connection, preparedStatement);
			return layoutJson;
		}

		if(resultSet.next()) {
			layoutJson = resultSet.getString("layoutJSON");
		}
		closeResources(connection, preparedStatement, resultSet);
		return layoutJson;
	}

	public Integer getLayoutIdByUserId(Integer userId) throws SQLException, ClassNotFoundException {
		Connection connection =null;
		PreparedStatement preparedStatement =null;
		ResultSet resultSet = null;
		Integer layoutId = null;

		connection = databaseManager.getConnection();
		String sql = "SELECT LayoutId FROM "+Constants.applicationProperties.getProperty("db.user.table.name")+"  WHERE UserId=?";
		if(connection==null)  {
			return layoutId;
		}
		preparedStatement  = connection.prepareStatement(sql);
		if(preparedStatement==null)  {
			connection.close();
			return layoutId;
		}
		preparedStatement.setInt(1, userId);

		resultSet = preparedStatement.executeQuery();
		if(resultSet == null)  {
			closeResources(connection, preparedStatement);
			return layoutId;
		}

		if(resultSet.next()) {
			layoutId = resultSet.getInt("LayoutId");
		}
		closeResources(connection, preparedStatement, resultSet);
		return layoutId;
	}
	
	public List<Layout> getLayoutListByUserId(Integer userId) throws SQLException, ClassNotFoundException {
		Connection connection =null;
		PreparedStatement preparedStatement =null;
		ResultSet resultSet = null;
		List<Layout> layoutList = null;

		connection = databaseManager.getConnection();
		String sql = "SELECT LayoutId, LayoutName FROM "+Constants.applicationProperties.getProperty("db.application.layout.table.name")+"  WHERE UserId=?";
		if(connection==null)  {
			return layoutList;
		}
		preparedStatement  = connection.prepareStatement(sql);
		if(preparedStatement==null)  {
			connection.close();
			return layoutList;
		}
		preparedStatement.setInt(1, userId);

		resultSet = preparedStatement.executeQuery();
		if(resultSet == null)  {
			closeResources(connection, preparedStatement);
			return layoutList;
		}

		layoutList = new ArrayList<Layout>();
		while(resultSet.next()) {
			layoutList.add(new Layout(resultSet.getInt("LayoutId"), resultSet.getString("LayoutName")));
		}
		closeResources(connection, preparedStatement, resultSet);
		return layoutList;
	}
	public Boolean deleteLayoutByLayoutId(Integer layoutId) throws SQLException, ClassNotFoundException {
		Connection connection = null;
		int count=0;
		String sql = "DELETE from "+Constants.applicationProperties.getProperty("db.application.layout.table.name")+
				" where LayoutId =? ;";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		connection = databaseManager.getConnection();
		if(connection==null) {
			return false;
		}
		preparedStatement = null;
		preparedStatement = connection.prepareStatement(sql);
		if(preparedStatement == null) {
			connection.close();
			return false;
		}
		preparedStatement.setInt(1, layoutId);
		
		count = preparedStatement.executeUpdate();
		closeResources(connection, preparedStatement,resultSet);
		if(count > 0) {
			return true;
		}
		else {
			return false;
		}
	}
}

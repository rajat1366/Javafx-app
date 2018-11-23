package com.fousalert.service;

import java.sql.SQLException;
import java.util.List;

import com.fousalert.bean.Layout;
import com.fousalert.dao.UserDAO;

public class UserService {

	private UserDAO userDAO;
	public UserService(){
		userDAO = new UserDAO();
	}
	public boolean insertUserDetails(Integer userId,String userName ) throws ClassNotFoundException, SQLException
	{
		return userDAO.createUserAndDetails(userId,userName);
	}
	public Integer fetchUserId(String userName) throws ClassNotFoundException, SQLException
	{  
		return userDAO.getUserId(userName);
	}
	public String fetchChartPreferences(Integer userId) throws ClassNotFoundException, SQLException {
		return userDAO.getChartPreferences(userId);
	}
	public Boolean createChartPreferencesByUserId(Integer userId,String chartPreferences) {
		return userDAO.createChartPreferencesByUserId(userId,chartPreferences);
	}
	public boolean setChartPreferences(Integer userId,String chartPreferences) throws ClassNotFoundException, SQLException
	{
		return userDAO.setChartPreferences(userId, chartPreferences);
	}
	public List<String> getSubscribedTickers(Integer userId) throws ClassNotFoundException, SQLException
	{
		return userDAO.getSubscribedTickers(userId);
	}
	public boolean saveTicker(String tickerSymbol,Integer userId) throws ClassNotFoundException, SQLException
	{
		return userDAO.addNewTicker(tickerSymbol,userId);
	}
	public boolean  deleteTicker(String tickerSymbol, Integer userId) throws ClassNotFoundException, SQLException
	{
		return userDAO.removeTicker(tickerSymbol,userId);
	}
	public boolean insertMasterTickerDetails(String symbolName, Integer userId) throws ClassNotFoundException, SQLException
	{
		return userDAO.insertMasterTickerData(symbolName, userId);
	}
	public List<String> fetchMasterTickerByUserId(Integer userId) throws ClassNotFoundException, SQLException
	{  
		return userDAO.fetchMasterTickerByUserId(userId);
	}
	public List<Layout> fetchLayoutListByUserId(Integer userID) throws ClassNotFoundException, SQLException {
		return userDAO.getLayoutListByUserId(userID);
	}
	public Integer saveLayoutListByUserId(Integer userId, String layoutJson, String layoutName) {
		return userDAO.addNewLayout(userId, layoutJson, layoutName);
	}
	public boolean saveLayoutIdByUserId(Integer userId, Integer layoutId) throws ClassNotFoundException, SQLException {
		return userDAO.setUserLayoutId(userId, layoutId);
	}
	public boolean updateLayoutJson(Integer layoutId, String layoutJson) throws ClassNotFoundException, SQLException {
		return userDAO.updateLayoutJson(layoutId, layoutJson);
	}
	public String getLayoutJsonByLayoutId(Integer layoutId) throws ClassNotFoundException, SQLException {
		return userDAO.getLayoutJsonByLayoutId(layoutId);
	}
	public Integer getLayoutIdByUserId(Integer userId) throws ClassNotFoundException, SQLException {
		return userDAO.getLayoutIdByUserId(userId);
	}
	public Boolean deleteLayoutByLayoutId(Integer layoutId) throws ClassNotFoundException, SQLException {
		return userDAO.deleteLayoutByLayoutId(layoutId);
	}
}

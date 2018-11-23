package com.fousalert.service;

import java.sql.SQLException;

import com.fousalert.dao.BaseDAO;

public class BaseService {

	private BaseDAO baseDAO = new BaseDAO();
	
	public boolean createDB() throws ClassNotFoundException, SQLException{
		return baseDAO.createDB();
	}
}

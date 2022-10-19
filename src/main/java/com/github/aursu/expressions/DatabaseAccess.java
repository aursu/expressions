package com.github.aursu.expressions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

enum DBDriver {
	MYSQL,
	POSTGRES
}

public class DatabaseAccess {
	private Connection connect = null;
	private String dbName, dbHost, dbUser, dbPassowrd, dbURL;

	public DatabaseAccess(String dbName, String dbHost, String dbUser, String dbPassowrd) {
		this.dbName = dbName;
		this.dbHost = dbHost;
		this.dbUser = dbUser;
		this.dbPassowrd = dbPassowrd;
	}

	public void connectMySQL() {
		dbURL = String.format("jdbc:mysql://%s/%s", dbHost, dbName);
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		try {
			connect = DriverManager.getConnection(dbURL, dbUser, dbPassowrd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PreparedStatement prepare(String sql) throws SQLException {
		return connect.prepareStatement(sql);
	}

	public ResultSet query(String sql) throws SQLException {
		Statement stmt = connect.createStatement();
		return stmt.executeQuery(sql);
	}

	public int execute(String sql) throws SQLException {
		Statement stmt = connect.createStatement();
		return stmt.executeUpdate(sql);
	}

	public boolean isConnected() {
		if (connect == null) return false;
		
		try {
			return connect.isValid(0);
		} catch (SQLException e) {
			e.printStackTrace();
			return  false;
		}
	}
}

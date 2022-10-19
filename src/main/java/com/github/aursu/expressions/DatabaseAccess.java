package com.github.aursu.expressions;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
	private DatabaseMetaData meta = null;
	
	private String dbName, dbHost, dbUser, dbPassowrd, dbURL;
	private String schema = null;

	public DatabaseAccess(String dbName, String dbHost, String dbUser, String dbPassowrd) {
		setup(dbName, dbHost, dbUser, dbPassowrd);
	}

	public void setup(String dbName, String dbHost, String dbUser, String dbPassowrd) {
		this.dbName = dbName;
		this.dbHost = dbHost;
		this.dbUser = dbUser;
		this.dbPassowrd = dbPassowrd;
	}

	public void connectMySQL() {
		schema = "jdbc:mysql";
		dbURL = String.format("%s://%s/%s", schema, dbHost, dbName);
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		try {
			connect = DriverManager.getConnection(dbURL, dbUser, dbPassowrd);
			meta = connect.getMetaData();
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

	public String getURL() {
		if (meta == null) return null;
		try {
			return meta.getURL();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getUserName() {
		if (meta == null) return null;
		try {
			return meta.getUserName();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void close() {
		try {
			if (connect == null || connect.isClosed()) return;
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean checkConnection() {
		// no connection - no schema
		if (schema == null) return false;

		// compile desired credentials (using last used schema for DB connection)
		String connectionURL = String.format("%s://%s/%s", schema, dbHost, dbName);
		String dbUserName = String.format("%s@%s", dbUser, dbHost);

		if (isConnected()) {			
			if (connectionURL.equals(getURL()) && dbUserName.equals(getUserName())) {
				return true;
			}
		}

		return  false;
	}
}

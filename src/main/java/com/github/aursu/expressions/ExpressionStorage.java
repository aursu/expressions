package com.github.aursu.expressions;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class ExpressionStorage extends DatabaseAccess {
	public static final double tolerance = 1.0e-12;

	public ExpressionStorage(String dbName, String dbHost, String dbUser, String dbPassowrd) {
		super(dbName, dbHost, dbUser, dbPassowrd);
	}

	public Vector<Vector<Object>> load() {
		return load("SELECT * FROM expressions;");
	}

	public Vector<Vector<Object>> lookup(double lkpValue, OperatorToken op) {
		String sql;

		switch(op.getValue()) {
			case CMP_GT:
				sql = "SELECT * FROM expressions WHERE Value > ?";
				break;
			case CMP_LT:
				sql = "SELECT * FROM expressions WHERE Value < ?";
				break;
			case CMP_GTE:
				sql = "SELECT * FROM expressions WHERE Value > ? OR ABS(Value - ?) < ?";
				break;
			case CMP_LTE:
				sql = "SELECT * FROM expressions WHERE Value < ? OR ABS(Value - ?) < ?";
				break;
			// https://dev.mysql.com/doc/refman/8.0/en/problems-with-float.html
			case EQ_NE:
				sql = "SELECT * FROM expressions WHERE ABS(Value - ?) > ?";
				break;
			case EQ_EQ:
			default:
				sql = "SELECT * FROM expressions WHERE ABS(Value - ?) < ?";
		}
		
		PreparedStatement pstmt;
		ParameterMetaData pstmtmeta;

		if (this.isConnected()) {
			try {
				pstmt = prepare(sql);
				pstmtmeta = pstmt.getParameterMetaData();

				pstmt.setDouble(1, lkpValue);
				// parameters count
				switch(pstmtmeta.getParameterCount()) {
					case 2:
						pstmt.setDouble(2, ExpressionStorage.tolerance);
						break;
					case 3:
						pstmt.setDouble(2, lkpValue);
						pstmt.setDouble(3, ExpressionStorage.tolerance);
				}

				ResultSet rs = pstmt.executeQuery();
				Vector<Vector<Object>> rowData = readResult(rs);
				pstmt.close();
				
				return rowData;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public int delete(String prn) {
		String sql = "DELETE FROM expressions WHERE Polish = ?";

		try {
			PreparedStatement pstmt = prepare(sql);
			pstmt.setString(1, prn);
			
			int rows = pstmt.executeUpdate();
			pstmt.close();
			
			return rows;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int store(String prn, String infix, Number evalValue) throws SQLException {		
		return store(prn, infix, evalValue.doubleValue());
	}

	public int store(String prn, String infix, double evalValue) throws SQLException {
		String sql = "INSERT INTO expressions VALUES (?, ?, ?)";

		PreparedStatement pstmt = prepare(sql);
		pstmt.setString(1, prn);
		pstmt.setString(2, infix);
		pstmt.setDouble(3, evalValue);
		
		int rows = pstmt.executeUpdate();
		pstmt.close();
		
		return rows;
	}

	private Vector<Vector<Object>> load(String sql) {
		if (this.isConnected()) {
			try {
				ResultSet rs = query(sql);

				return readResult(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Vector<Vector<Object>> readResult(ResultSet rs) throws SQLException {
		Vector<Vector<Object>> rowData = new Vector<>();

		while(rs.next()) {
			Vector<Object> row = new Vector<>();
            int numColumns = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= numColumns; i++) {
                // Column numbers start at 1.
                // Also there are many methods on the result set to return
                // the column as a particular type. Refer to the Sun documentation
                // for the list of valid conversions.
            	row.add(rs.getObject(i));
            }
            rowData.add(row);
		}
		rs.close();

		return rowData;
	}
}

package edu.upb.winfo.download.com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.*;
import java.io.*;
import java.sql.BatchUpdateException;
import java.sql.DatabaseMetaData;
import java.sql.RowIdLifetime;
import java.sql.SQLWarning;

public class Database {

	public String dbms;
	public String jarFile;
	public String dbName;
	public String userName;
	public String password;
	public String urlString;

	private String driver;
	private String serverName;
	private int portNumber;
	private Properties prop;

	public Database(String propertiesFileName)
			throws FileNotFoundException, IOException, InvalidPropertiesFormatException {
		super();
		this.setProperties(propertiesFileName);
	}

	public static void getWarningsFromResultSet(ResultSet rs) throws SQLException {
		Database.printWarnings(rs.getWarnings());
	}

	public static void getWarningsFromStatement(Statement stmt) throws SQLException {
		Database.printWarnings(stmt.getWarnings());
	}

	public static void printWarnings(SQLWarning warning) throws SQLException {
		if (warning != null) {
			System.out.println("\n---Warning---\n");
			while (warning != null) {
				System.out.println("Message: " + warning.getMessage());
				System.out.println("SQLState: " + warning.getSQLState());
				System.out.print("Vendor error code: ");
				System.out.println(warning.getErrorCode());
				System.out.println("");
				warning = warning.getNextWarning();
			}
		}
	}

	private void setProperties(String fileName)
			throws FileNotFoundException, IOException, InvalidPropertiesFormatException {
		this.prop = new Properties();
		FileInputStream fis = new FileInputStream(fileName);
		prop.loadFromXML(fis);

		this.dbms = this.prop.getProperty("dbms");
		this.jarFile = this.prop.getProperty("jar_file");
		this.driver = this.prop.getProperty("driver");
		this.dbName = this.prop.getProperty("database_name");
		this.userName = this.prop.getProperty("user_name");
		this.password = this.prop.getProperty("password");
		this.serverName = this.prop.getProperty("server_name");
		this.portNumber = Integer.parseInt(this.prop.getProperty("port_number"));

		System.out.println("Set the following properties:");
		System.out.println("dbms: " + dbms);
		System.out.println("driver: " + driver);
		System.out.println("dbName: " + dbName);
		System.out.println("userName: " + userName);
		System.out.println("serverName: " + serverName);
		System.out.println("portNumber: " + portNumber);
	}

	public Connection getConnection() throws SQLException {

		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		if (this.dbms.equals("mysql")) {
			conn = DriverManager.getConnection(
					"jdbc:" + this.dbms + "://" + this.serverName + ":" + this.portNumber + "/", connectionProps);
		} else if (this.dbms.equals("derby")) {
			conn = DriverManager.getConnection("jdbc:" + this.dbms + ":" + this.dbName + ";create=true",
					connectionProps);
		}
		System.out.println("Connected to database");
		return conn;
	}

	public Connection getConnectionToDatabase() throws SQLException {
		{
			Connection conn = null;
			Properties connectionProps = new Properties();
			connectionProps.put("user", this.userName);
			connectionProps.put("password", this.password);

			if (this.dbms.equals("mysql")) {
				conn = DriverManager.getConnection(
						"jdbc:" + dbms + "://" + serverName + ":" + portNumber + "/" + dbName, connectionProps);
				conn.setCatalog(this.dbName);
			} else if (this.dbms.equals("derby")) {
				conn = DriverManager.getConnection("jdbc:" + dbms + ":" + dbName, connectionProps);
			}
			System.out.println("Connected to database");
			return conn;
		}
	}

	public static void closeConnection(Connection connArg) {
		System.out.println("Releasing all open resources ...");
		try {
			if (connArg != null) {
				connArg.close();
				connArg = null;
			}
		} catch (SQLException sqle) {
			printSQLException(sqle);
		}
	}

	public static void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				if (ignoreSQLException(((SQLException) e).getSQLState()) == false) {
					e.printStackTrace(System.err);
					System.err.println("SQLState: " + ((SQLException) e).getSQLState());
					System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
					System.err.println("Message: " + e.getMessage());
					Throwable t = ex.getCause();
					while (t != null) {
						System.out.println("Cause: " + t);
						t = t.getCause();
					}
				}
			}
		}
	}

	public static boolean ignoreSQLException(String sqlState) {
		if (sqlState == null) {
			System.out.println("The SQL state is not defined!");
			return false;
		}
		// X0Y32: Jar file already exists in schema
		if (sqlState.equalsIgnoreCase("X0Y32"))
			return true;
		// 42Y55: Table already exists in schema
		if (sqlState.equalsIgnoreCase("42Y55"))
			return true;
		return false;
	}

}

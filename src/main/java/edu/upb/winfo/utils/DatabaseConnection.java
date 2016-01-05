package edu.upb.winfo.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Properties;
import java.util.*;
import java.io.*;

/**
 * Created by geskill on 02.01.2016.
 *
 * This is copied from the JDBC tutorial:
 * https://docs.oracle.com/javase/tutorial/jdbc/basics/
 *
 * @author geskill
 */
public class DatabaseConnection {

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

	public DatabaseConnection(String propertiesFileName)
			throws FileNotFoundException, IOException, InvalidPropertiesFormatException {
		super();
		this.setProperties(propertiesFileName);
	}

	public static void getWarningsFromResultSet(ResultSet rs) throws SQLException {
		DatabaseConnection.printWarnings(rs.getWarnings());
	}

	public static void getWarningsFromStatement(Statement stmt) throws SQLException {
		DatabaseConnection.printWarnings(stmt.getWarnings());
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

	/**
	 * Using the class loader to dynamically load the JAR package with JDBC drivers at runtime.
	 *
	 * This is copied from:
	 * http://codereview.stackexchange.com/questions/70669/loading-the-jdbc-driver-jar-dynamically-from-external-location
	 *
	 * @throws SQLException
	 */
	private void doInit() throws SQLException {

		final URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();

		Method method = null;
		try {
			method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(loader, new File(this.jarFile).toURI().toURL());

			Class<?> classToLoad = Class.forName(this.driver, true, loader);
			Driver driver = (Driver) classToLoad.newInstance();
			DriverManager.registerDriver(new DriverShim(driver));

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException {
		this.doInit();
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
		this.doInit();
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

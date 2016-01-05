package edu.upb.winfo.utils;

import uk.org.lidalia.slf4jext.Logger;
import uk.org.lidalia.slf4jext.LoggerFactory;

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

	protected static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

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
			logger.warn("\n---Warning---\n");
			while (warning != null) {
				logger.warn("Message: " + warning.getMessage());
				logger.warn("SQLState: " + warning.getSQLState());
				logger.warn("Vendor error code: " + warning.getErrorCode());
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

		logger.info("Set the following properties:");
		logger.info("dbms: " + dbms);
		logger.info("driver: " + driver);
		logger.info("dbName: " + dbName);
		logger.info("userName: " + userName);
		logger.info("serverName: " + serverName);
		logger.info("portNumber: " + portNumber);
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

	/**
	 *
	 * @return
	 * @throws SQLException
	 */
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
		logger.info("Connected to database");
		return conn;
	}

	/**
	 *
	 * @return
	 * @throws SQLException
	 */
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
		logger.info("Connected to database");
		return conn;
	}

	/**
	 *
	 * @param connArg
	 */
	public static void closeConnection(Connection connArg) {
		logger.info("Releasing all open resources ...");
		try {
			if (connArg != null) {
				connArg.close();
				connArg = null;
			}
		} catch (SQLException sqle) {
			printSQLException(sqle);
		}
	}

	/**
	 *
	 * @param ex
	 */
	public static void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				if (ignoreSQLException(((SQLException) e).getSQLState()) == false) {
					e.printStackTrace(System.err);
					logger.error("SQLState: " + ((SQLException) e).getSQLState());
					logger.error("Error Code: " + ((SQLException) e).getErrorCode());
					logger.error("Message: " + e.getMessage());
					Throwable t = ex.getCause();
					while (t != null) {
						logger.error("Cause: " + t);
						t = t.getCause();
					}
				}
			}
		}
	}

	/**
	 *
	 * @param sqlState
	 * @return
	 */
	public static boolean ignoreSQLException(String sqlState) {
		if (sqlState == null) {
			logger.info("The SQL state is not defined!");
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

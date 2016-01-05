package edu.upb.winfo.utils;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by geskill on 02.01.2016.
 *
 * This is some hack, to work around some Java issue for registering
 * the JAR package with JDBC drivers at runtime.
 *
 * Can be found here:
 * http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
 * https://code.google.com/p/starschema-bigquery-jdbc/source/browse/src/main/java/net/starschema/clouddb/initializer/DriverShim.java?r=af9c1199674e94dde7b8db8e9d4d8de3a86956d9
 *
 * @author geskill
 */
public class DriverShim implements Driver {

	private Driver driver;

	DriverShim(Driver driver) {
		this.driver = driver;
	}

	public Connection connect(String url, Properties info) throws SQLException {
		return this.driver.connect(url, info);
	}

	public boolean acceptsURL(String url) throws SQLException {
		return this.driver.acceptsURL(url);
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return this.driver.getPropertyInfo(url, info);
	}

	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}

	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}

	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.driver.getParentLogger();
	}
}

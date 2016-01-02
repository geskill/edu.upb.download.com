package edu.upb.winfo.download.com;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by geskill on 02.01.2016.
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

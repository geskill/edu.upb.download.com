package edu.upb.winfo.download.com;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductTable {

	private String dbName;
	private Connection con;
	private String dbms;

	public ProductTable(Connection connArg, String dbNameArg, String dbmsArg) {
		super();
		this.con = connArg;
		this.dbName = dbNameArg;
		this.dbms = dbmsArg;
	}

	public void updateProduct(int pid, int oid, String editors_review_name, Date editors_review_date,
			String editors_review_description, String editors_review_text, int editors_review_rating,
			int users_review_rating, int users_review_rating_count, String publisher_description, String publisher_name,
			String publisher_url, Date publish_date, String platform, String category, String subcategory,
			int latest_id_v) throws SQLException {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			if (!stmt.execute("SELECT pid FROM download_com_products " + "WHERE pid = '" + pid + "'")) {
				stmt.executeUpdate(
						"INSERT INTO `download_com_products`(`pid`, `oid`, `editors_review_name`, `editors_review_date`, `editors_review_description`, `editors_review_text`, `editors_review_rating`, `users_review_rating`, `users_review_rating_count`, `publisher_description`, `publisher_name`, `publisher_url`, `publish_date`, `platform`, `category`, `subcategory`, `latest_id_v`) "
								+ "VALUES('" + pid + "', '" + oid + "', '" + editors_review_name + "', '"
								+ editors_review_date + "', '" + editors_review_description + "', '"
								+ editors_review_text + "', '" + editors_review_rating + "', '" + users_review_rating
								+ "', '" + users_review_rating_count + "', '" + publisher_description + "', '"
								+ publisher_name + "', '" + publisher_url + "', '" + publish_date + "', '" + platform
								+ "', '" + category + "', '" + subcategory + "', '" + latest_id_v + ")");
			} else {
				stmt.executeUpdate("UPDATE `download_com_products` SET `oid`='" + oid + "',`editors_review_name`='"
						+ editors_review_name + "',`editors_review_date`='" + editors_review_date
						+ "',`editors_review_description`='" + editors_review_description + "',`editors_review_text`='"
						+ editors_review_text + "',`editors_review_rating`='" + editors_review_rating
						+ "',`users_review_rating`='" + users_review_rating + "',`users_review_rating_count`='"
						+ users_review_rating_count + "',`publisher_description`='" + publisher_description
						+ "',`publisher_name`='" + publisher_name + "',`publisher_url`='" + publisher_url
						+ "',`publish_date`='" + publish_date + "',`platform`='" + platform + "',`category`='"
						+ category + "',`subcategory`='" + subcategory + "',`latest_id_v`='" + latest_id_v
						+ "' WHERE pid = '" + pid + "'");
			}

		} catch (SQLException e) {
			Database.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

}

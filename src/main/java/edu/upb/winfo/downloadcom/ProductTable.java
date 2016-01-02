package edu.upb.winfo.downloadcom;

import edu.upb.winfo.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by geskill on 30.12.2015.
 *
 * @author geskill
 */
public class ProductTable {

	private Connection con;

	public ProductTable(Connection connArg) {
		super();
		this.con = connArg;
	}

	public boolean hasProduct(int pid) throws SQLException {
		boolean result = false;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			result = stmt.execute("SELECT pid FROM download_com_products " + "WHERE pid = '" + pid + "'");

		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return result;
	}

	public void updateProduct(int pid, int oid, String editors_review_name, Date editors_review_date,
	                          String editors_review_description, String editors_review_text,
	                          double editors_review_rating, double users_review_rating,
	                          int users_review_rating_count, String publisher_description, String publisher_name,
	                          String publisher_url, String platform, String category,
	                          String subcategory, int latest_id_v) throws SQLException {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			if (!hasProduct(pid)) {
				stmt.executeUpdate(
						"INSERT INTO `download_com_products`(`pid`, `oid`, `editors_review_name`, "
								+ "`editors_review_date`, `editors_review_description`, `editors_review_text`, "
								+ "`editors_review_rating`, `users_review_rating`, `users_review_rating_count`, "
								+ "`publisher_description`, `publisher_name`, `publisher_url`, `platform`, "
								+ "`category`, `subcategory`, `latest_id_v`) "
								+ "VALUES('" + pid + "', '" + oid + "', '" + editors_review_name + "', '"
								+ editors_review_date + "', '" + editors_review_description + "', '"
								+ editors_review_text + "', '" + editors_review_rating + "', '" + users_review_rating
								+ "', '" + users_review_rating_count + "', '" + publisher_description + "', '"
								+ publisher_name + "', '" + publisher_url + "', '" + platform
								+ "', '" + category + "', '" + subcategory + "', '" + latest_id_v + "')");
			} else {
				stmt.executeUpdate("UPDATE `download_com_products` SET `oid`='" + oid + "',`editors_review_name`='"
						+ editors_review_name + "',`editors_review_date`='" + editors_review_date
						+ "',`editors_review_description`='" + editors_review_description + "',`editors_review_text`='"
						+ editors_review_text + "',`editors_review_rating`='" + editors_review_rating
						+ "',`users_review_rating`='" + users_review_rating + "',`users_review_rating_count`='"
						+ users_review_rating_count + "',`publisher_description`='" + publisher_description
						+ "',`publisher_name`='" + publisher_name + "',`publisher_url`='" + publisher_url
						+ "',`platform`='" + platform + "',`category`='" + category + "',`subcategory`='"
						+ subcategory + "',`latest_id_v`='" + latest_id_v + "' WHERE pid = '" + pid + "'");
			}

		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

}

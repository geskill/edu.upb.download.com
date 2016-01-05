package edu.upb.winfo.downloadcom;

import edu.upb.winfo.utils.DatabaseConnection;
import edu.upb.winfo.utils.DatabaseTable;

import java.sql.*;

/**
 * Created by geskill on 30.12.2015.
 *
 * @author geskill
 */
public class ProductTable extends DatabaseTable {

	public ProductTable(Connection connArg) {
		super(connArg);
	}

	public boolean hasProduct(int pid) throws SQLException {
		int count = 0;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			stmt = con.prepareStatement("SELECT count(*) FROM `download_com_products` WHERE `pid`=?");
			stmt.setInt(1, pid);
			rset = stmt.executeQuery();
			if (rset.next()) {
				count = rset.getInt(1);
			}

		} catch (SQLException e) {
			logger.error("Error on hasProduct(" + pid + ")");
			DatabaseConnection.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return count > 0;
	}

	public void updateProduct(int pid, int oid, String editors_review_name, Date editors_review_date,
	                          String editors_review_description, String editors_review_text,
	                          double editors_review_rating, double users_review_rating,
	                          int users_review_rating_count, String publisher_description,
	                          String publisher_description_alterations, String publisher_name, String publisher_url,
	                          String platform, String category, String subcategory,
	                          int latest_id_v) throws SQLException {
		int index = 1;
		String query = "";
		PreparedStatement stmt = null;
		try {
			if (!hasProduct(pid)) {
				query = "INSERT INTO `download_com_products`(`pid`, `oid`, `editors_review_name`, " +
						"`editors_review_date`, `editors_review_description`, `editors_review_text`, " +
						"`editors_review_rating`, `users_review_rating`, `users_review_rating_count`, " +
						"`publisher_description`, `publisher_description_alterations`, `publisher_name`, " +
						"`publisher_url`, `platform`, `category`, `subcategory`, `latest_id_v`) VALUES (?,?,?,?,?,?,?," +
						"?,?,?,?,?,?,?,?,?,?)";
				stmt = con.prepareStatement(query);
				stmt.setInt(index++, pid);
				stmt.setInt(index++, oid);
				stmt.setString(index++, editors_review_name);
				if (editors_review_date == null) {
					stmt.setNull(index++, Types.DATE);
				} else {
					stmt.setDate(index++, editors_review_date);
				}
				stmt.setString(index++, editors_review_description);
				stmt.setString(index++, editors_review_text);
				stmt.setDouble(index++, editors_review_rating);
				stmt.setDouble(index++, users_review_rating);
				stmt.setInt(index++, users_review_rating_count);
				stmt.setString(index++, publisher_description);
				stmt.setString(index++, publisher_description_alterations);
				stmt.setString(index++, publisher_name);
				stmt.setString(index++, publisher_url);
				stmt.setString(index++, platform);
				stmt.setString(index++, category);
				stmt.setString(index++, subcategory);
				stmt.setInt(index++, latest_id_v);

			} else {
				query = "UPDATE `download_com_products` SET `oid`=?,`editors_review_name`=?,`editors_review_date`=?," +
						"`editors_review_description`=?,`editors_review_text`=?,`editors_review_rating`=?," +
						"`users_review_rating`=?,`users_review_rating_count`=?,`publisher_description`=?," +
						"`publisher_description_alterations`=?,`publisher_name`=?,`publisher_url`=?,`platform`=?," +
						"`category`=?,`subcategory`=?,`latest_id_v`=? WHERE `pid`=?";
				stmt = con.prepareStatement(query);
				stmt.setInt(index++, oid);
				stmt.setString(index++, editors_review_name);
				if (editors_review_date == null) {
					stmt.setNull(index++, Types.DATE);
				} else {
					stmt.setDate(index++, editors_review_date);
				}
				stmt.setString(index++, editors_review_description);
				stmt.setString(index++, editors_review_text);
				stmt.setDouble(index++, editors_review_rating);
				stmt.setDouble(index++, users_review_rating);
				stmt.setInt(index++, users_review_rating_count);
				stmt.setString(index++, publisher_description);
				stmt.setString(index++, publisher_description_alterations);
				stmt.setString(index++, publisher_name);
				stmt.setString(index++, publisher_url);
				stmt.setString(index++, platform);
				stmt.setString(index++, category);
				stmt.setString(index++, subcategory);
				stmt.setInt(index++, latest_id_v);
				stmt.setInt(index++, pid);

			}
			logger.info("query: " + stmt);
			// System.out.println("query: " + stmt);
			stmt.executeUpdate();

		} catch (SQLException e) {
			logger.error("Error on updateProduct(" + pid + ")");
			DatabaseConnection.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

}

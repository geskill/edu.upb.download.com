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
public class ProductUserReviewTable {

	private Connection con;

	public ProductUserReviewTable(Connection connArg) {
		super();
		this.con = connArg;
	}

	public boolean hasProductUserReview(int mid, int id_p, int id_v) throws SQLException {
		boolean result = false;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			result = stmt.execute("SELECT mid FROM download_com_product_user_reviews " + "WHERE mid = '" + mid
					+ "' AND id_p = '" + id_p + "' AND id_v = '" + id_v + "'");

		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return result;
	}

	public void updateProductUserReview(int mid, int id_p, int id_v, double rating, String title, String author,
	                                    Date date, String pros, String cons, String summary, int thumbs_up,
	                                    int thumbs_down) throws SQLException {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			if (!hasProductUserReview(mid, id_p, id_v)) {
				stmt.executeUpdate(
						"INSERT INTO `download_com_product_user_reviews`(`mid`, `id_p`, `id_v`, `rating`, `title`, "
								+ "`author`, `date`, `pros`, `cons`, `summary`, `thumbs_up`, `thumbs_down`) "
								+ "VALUES('" + mid + "', '" + id_p + "', '" + id_v + "', '" + rating + "', '" + title
								+ "', '" + author + "', '" + date + "', '" + pros + "', '" + cons + "', '" + summary
								+ "', '" + thumbs_up + "', '" + thumbs_down + "')");
			} else {
				stmt.executeUpdate("UPDATE `download_com_product_user_reviews` SET `rating`='" + rating + "',`title`='"
						+ title + "',`author`='" + author + "',`date`='" + date + "',`pros`='" + pros + "',`cons`='"
						+ cons + "',`summary`='" + summary + "',`thumbs_up`='" + thumbs_up + "',`thumbs_down`='"
						+ thumbs_down + "' WHERE mid = '" + mid + "' AND id_p = '" + id_p + "' AND id_v = '" + id_v
						+ "'");
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

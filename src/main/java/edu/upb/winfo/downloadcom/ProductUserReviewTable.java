package edu.upb.winfo.downloadcom;

import edu.upb.winfo.utils.DatabaseConnection;
import edu.upb.winfo.utils.DatabaseTable;

import java.sql.*;

/**
 * Created by geskill on 30.12.2015.
 *
 * @author geskill
 */
public class ProductUserReviewTable extends DatabaseTable {

	public ProductUserReviewTable(Connection connArg) {
		super(connArg);
	}

	/**
	 * Checks if the user review for the product version of a product set is in the database
	 *
	 * @param mid message id
	 * @param id_p product set id
	 * @param id_v product version id
	 * @return true if exists
	 */
	public boolean hasProductUserReview(int mid, int id_p, int id_v) throws SQLException {
		int count = 0;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			stmt = con.prepareStatement("SELECT count(*) FROM `download_com_product_user_reviews` " +
					"WHERE `mid`=? AND `id_v`=?");
			stmt.setInt(1, mid);
			stmt.setInt(2, id_v);
			rset = stmt.executeQuery();
			if (rset.next()) {
				count = rset.getInt(1);
			}

		} catch (SQLException e) {
			logger.error("Error on hasProductUserReview(" + mid + ")");
			DatabaseConnection.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return count > 0;
	}

	/**
	 * Adds or edits a product user review to the database
	 *
	 * @param mid message id
	 * @param id_p product set id
	 * @param id_v product version id
	 * @param rating double
	 * @param title string
	 * @param author string
	 * @param date date
	 * @param pros string
	 * @param cons string
	 * @param summary string
	 * @param thumbs_up integer
	 * @param thumbs_down integer
	 */
	public void updateProductUserReview(int mid, int id_p, int id_v, double rating, String title, String author,
	                                    Date date, String pros, String cons, String summary, int thumbs_up,
	                                    int thumbs_down) throws SQLException {
		int index = 1;
		String query = "";
		PreparedStatement stmt = null;
		try {
			if (!hasProductUserReview(mid, id_p, id_v)) {
				query = "INSERT INTO `download_com_product_user_reviews`(`mid`, `id_p`, `id_v`, `rating`, `title`, " +
						"`author`, `date`, `pros`, `cons`, `summary`, `thumbs_up`, `thumbs_down`) VALUES (?,?,?,?,?," +
						"?,?,?,?,?,?,?)";
				stmt = con.prepareStatement(query);
				stmt.setInt(index++, mid);
				stmt.setInt(index++, id_p);
				stmt.setInt(index++, id_v);
				stmt.setDouble(index++, rating);
				stmt.setString(index++, title);
				stmt.setString(index++, author);
				if (date == null) {
					stmt.setNull(index++, Types.DATE);
				} else {
					stmt.setDate(index++, date);
				}
				stmt.setString(index++, pros);
				stmt.setString(index++, cons);
				stmt.setString(index++, summary);
				stmt.setInt(index++, thumbs_up);
				stmt.setInt(index++, thumbs_down);

			} else {
				query = "UPDATE `download_com_product_user_reviews` SET `id_p`=?,`rating`=?,`title`=?,`author`=?,`date`=?," +
						"`pros`=?,`cons`=?,`summary`=?,`thumbs_up`=?,`thumbs_down`=? WHERE `mid`=? AND `id_v`=?";
				stmt = con.prepareStatement(query);
				stmt.setInt(index++, id_p);
				stmt.setDouble(index++, rating);
				stmt.setString(index++, title);
				stmt.setString(index++, author);
				if (date == null) {
					stmt.setNull(index++, Types.DATE);
				} else {
					stmt.setDate(index++, date);
				}
				stmt.setString(index++, pros);
				stmt.setString(index++, cons);
				stmt.setString(index++, summary);
				stmt.setInt(index++, thumbs_up);
				stmt.setInt(index++, thumbs_down);
				stmt.setInt(index++, mid);
				stmt.setInt(index++, id_v);

			}
			logger.info("query: " + stmt);
			// System.out.println("query: " + stmt);
			stmt.executeUpdate();

		} catch (SQLException e) {
			logger.error("Error on hasProductUserReview(" + mid + ")");
			DatabaseConnection.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
}

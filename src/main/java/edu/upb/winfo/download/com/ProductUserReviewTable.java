package edu.upb.winfo.download.com;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductUserReviewTable {

	private String dbName;
	private Connection con;
	private String dbms;

	public ProductUserReviewTable(Connection connArg, String dbNameArg, String dbmsArg) {
		super();
		this.con = connArg;
		this.dbName = dbNameArg;
		this.dbms = dbmsArg;
	}

	public void updateProductUserReview(int mid, int id_p, int id_v, String name, Date date, String pros, String cons,
			String summary, int thumbs_up, int thumbs_down) throws SQLException {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			if (!stmt.execute("SELECT mid FROM download_com_product_user_reviews " + "WHERE mid = '" + mid
					+ "' AND id_p = '" + id_p + "' AND id_v = '" + id_v + "'")) {
				stmt.executeUpdate(
						"INSERT INTO `download_com_product_user_reviews`(`mid`, `id_p`, `id_v`, `name`, `date`, `pros`, `cons`, `summary`, `thumbs_up`, `thumbs_down`) "
								+ "VALUES('" + mid + "', '" + id_p + "', '" + id_v + "', '" + name + "', '" + date
								+ "', '" + pros + "', '" + cons + "', '" + summary + "', '" + thumbs_up + "', '"
								+ thumbs_down + "')");
			} else {
				stmt.executeUpdate("UPDATE `download_com_product_user_reviews` SET `name`='" + name + "',`date`='"
						+ date + "',`pros`='" + pros + "',`cons`='" + cons + "',`summary`='" + summary
						+ "',`thumbs_up`='" + thumbs_up + "',`thumbs_down`='" + thumbs_down + "' WHERE mid = '" + mid
						+ "' AND id_p = '" + id_p + "' AND id_v = '" + id_v + "'");
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

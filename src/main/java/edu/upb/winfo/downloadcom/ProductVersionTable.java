package edu.upb.winfo.downloadcom;

import edu.upb.winfo.utils.DatabaseConnection;
import edu.upb.winfo.utils.DatabaseTable;

import java.sql.*;

/**
 * Created by geskill on 30.12.2015.
 *
 * @author geskill
 */
public class ProductVersionTable extends DatabaseTable {

	public ProductVersionTable(Connection connArg) {
		super(connArg);
	}

	public boolean hasProductVersion(int id_p, int vid) throws SQLException {
		int count = 0;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			stmt = con.prepareStatement("SELECT count(*) FROM `download_com_product_versions` WHERE `id_p`=? AND " +
					"`vid`=?");
			stmt.setInt(1, id_p);
			stmt.setInt(2, vid);
			rset = stmt.executeQuery();
			if (rset.next()) {
				count = rset.getInt(1);
			}

		} catch (SQLException e) {
			logger.error("Error on hasProductVersion(" + vid + ")");
			DatabaseConnection.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return count > 0;
	}

	public int getProductIDFromVersionID(int vid) throws SQLException {
		int result = 0;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			stmt = con.prepareStatement("SELECT `id_p` FROM `download_com_product_versions` WHERE `vid`=?");
			stmt.setInt(1, vid);
			rset = stmt.executeQuery();
			if (rset.next()) {
				result = rset.getInt(1);
			}

		} catch (SQLException e) {
			logger.error("Error on getProductIDFromVersionID(" + vid + ")");
			DatabaseConnection.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return result;
	}

	public void updateProductVersion(int id_p, int vid, String version_name, String version_alterations,
	                                 Date version_publish_date, Date version_added_date, String version_identifier,
	                                 String operating_systems, String additional_requirements, String download_size,
	                                 String download_name, String download_link, int downloads_total,
	                                 int downloads_last_week, String license_model, String license_limitations,
	                                 String license_cost)
			throws SQLException {
		int index = 1;
		String query = "";
		PreparedStatement stmt = null;
		try {
			if (!hasProductVersion(id_p, vid)) {
				query = "INSERT INTO `download_com_product_versions`(`id_p`, `vid`, `version_name`, " +
						"`version_alterations`, `version_publish_date`, `version_added_date`, `version_identifier`, " +
						"`operating_systems`, `additional_requirements`, `download_size`, `download_name`, " +
						"`download_link`, `downloads_total`, `downloads_last_week`, `license_model`, " +
						"`license_limitations`, `license_cost`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				stmt = con.prepareStatement(query);
				stmt.setInt(index++, id_p);
				stmt.setInt(index++, vid);
				stmt.setString(index++, version_name);
				stmt.setString(index++, version_alterations);
				if (version_publish_date == null) {
					stmt.setNull(index++, Types.DATE);
				} else {
					stmt.setDate(index++, version_publish_date);
				}
				if (version_added_date == null) {
					stmt.setNull(index++, Types.DATE);
				} else {
					stmt.setDate(index++, version_added_date);
				}
				stmt.setString(index++, version_identifier);
				stmt.setString(index++, operating_systems);
				stmt.setString(index++, additional_requirements);
				stmt.setString(index++, download_size);
				stmt.setString(index++, download_name);
				stmt.setString(index++, download_link);
				stmt.setInt(index++, downloads_total);
				stmt.setInt(index++, downloads_last_week);
				stmt.setString(index++, license_model);
				stmt.setString(index++, license_limitations);
				stmt.setString(index++, license_cost);

			} else {
				query = "UPDATE `download_com_product_versions` SET `version_name`=?,`version_alterations`=?," +
						"`version_publish_date`=?,`version_added_date`=?,`version_identifier`=?,`operating_systems`=?," +
						"`additional_requirements`=?,`download_size`=?,`download_name`=?,`download_link`=?," +
						"`downloads_total`=?,`downloads_last_week`=?,`license_model`=?,`license_limitations`=?," +
						"`license_cost`=? WHERE `id_p`=? AND `vid`=?";
				stmt = con.prepareStatement(query);
				stmt.setString(index++, version_name);
				stmt.setString(index++, version_alterations);
				if (version_publish_date == null) {
					stmt.setNull(index++, Types.DATE);
				} else {
					stmt.setDate(index++, version_publish_date);
				}
				if (version_added_date == null) {
					stmt.setNull(index++, Types.DATE);
				} else {
					stmt.setDate(index++, version_added_date);
				}
				stmt.setString(index++, version_identifier);
				stmt.setString(index++, operating_systems);
				stmt.setString(index++, additional_requirements);
				stmt.setString(index++, download_size);
				stmt.setString(index++, download_name);
				stmt.setString(index++, download_link);
				stmt.setInt(index++, downloads_total);
				stmt.setInt(index++, downloads_last_week);
				stmt.setString(index++, license_model);
				stmt.setString(index++, license_limitations);
				stmt.setString(index++, license_cost);
				stmt.setInt(index++, id_p);
				stmt.setInt(index++, vid);

			}
			logger.info("query: " + stmt);
			// System.out.println("query: " + stmt);
			stmt.executeUpdate();

		} catch (SQLException e) {
			logger.error("Error on updateProductVersion(" + vid + ")");
			DatabaseConnection.printSQLException(e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
}

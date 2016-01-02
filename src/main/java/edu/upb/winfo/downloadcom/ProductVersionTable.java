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
public class ProductVersionTable {

    private Connection con;

    public ProductVersionTable(Connection connArg) {
        super();
        this.con = connArg;
    }

	public boolean hasProductVersion(int id_p, int vid) throws SQLException {
		boolean result = false;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			result = stmt.execute("SELECT vid FROM download_com_product_versions " + "WHERE id_p = '" + id_p
					+ "' AND vid = '" + vid + "'");

		} catch (SQLException e) {
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
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            if (!hasProductVersion(id_p, vid)) {
                stmt.executeUpdate(
                        "INSERT INTO `download_com_product_versions`(`id_p`, `vid`, `version_name`, "
                                + "`version_alterations`, `version_publish_date`, `version_added_date`, "
                                + "`version_identifier`, `operating_systems`, `additional_requirements`, "
                                + "`download_size`, `download_name`, `download_link`, `downloads_total`, "
                                + "`downloads_last_week`, `license_model`, `license_limitations`, `license_cost`) "
                                + "VALUES('" + id_p + "', '" + vid + "', '" + version_name + "', '"
                                + version_alterations + "', '" + version_publish_date + "', '" + version_added_date
                                + "', '" + version_identifier + "', '" + operating_systems + "', '"
                                + additional_requirements + "', '" + download_size + "', '" + download_name + "', '"
                                + download_link + "', '" + downloads_total + "', '" + downloads_last_week + "', '"
                                + license_model + "', '" + license_limitations + "', '" + license_cost + "')");
            } else {
                stmt.executeUpdate("UPDATE `download_com_product_versions` SET `version_name`='" + version_name
                        + "',`version_alterations`='" + version_alterations + "',`version_publish_date`='"
                        + version_publish_date + "',`version_added_date`='" + version_added_date
                        + "',`version_identifier`='" + version_identifier + "',`operating_systems`='"
                        + operating_systems + "',`additional_requirements`='" + additional_requirements
                        + "',`download_size`='" + download_size + "',`download_name`='" + download_name
                        + "',`download_link`='" + download_link + "',`downloads_total`='" + downloads_total
                        + "',`downloads_last_week`='" + downloads_last_week + "',`license_model`='" + license_model
                        + "',`license_limitations`='" + license_limitations + "',`license_cost`='" + license_cost
                        + "' WHERE id_p = '" + id_p + "' AND vid = '" + vid + "'");
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

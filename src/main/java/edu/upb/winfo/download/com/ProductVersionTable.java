package edu.upb.winfo.download.com;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductVersionTable {

    private String dbName;
    private Connection con;
    private String dbms;

    public ProductVersionTable(Connection connArg, String dbNameArg, String dbmsArg) {
        super();
        this.con = connArg;
        this.dbName = dbNameArg;
        this.dbms = dbmsArg;
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
            if (!stmt.execute("SELECT vid FROM download_com_product_versions " + "WHERE id_p = '" + id_p
                    + "' AND vid = '" + vid + "'")) {
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
            Database.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

}

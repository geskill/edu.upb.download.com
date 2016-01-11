package edu.upb.winfo.downloadcom;

import edu.upb.winfo.utils.DatabaseConnection;
import uk.org.lidalia.slf4jext.Logger;
import uk.org.lidalia.slf4jext.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by geskill on 02.01.2016.
 *
 * @author geskill
 */
public class LocalDatabase implements DatabaseInterface {

	protected static final Logger logger = LoggerFactory.getLogger(LocalDatabase.class);

	private Connection connection = null;
	private ProductTable productTable = null;
	private ProductVersionTable productVersionTable = null;
	private ProductUserReviewTable productUserReviewTable = null;

	/**
	 * @param propertiesFileName Filename of the settings file
	 */
	public LocalDatabase(String propertiesFileName, AtomicBoolean success) {
		DatabaseConnection databaseConnection = null;
		try {
			databaseConnection = new DatabaseConnection(propertiesFileName);
		} catch (IOException e) {
			logger.error("Error reading database settings", e);
		}
		assert databaseConnection != null;
		try {
			this.connection = databaseConnection.getConnectionToDatabase();
		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		}

		this.productTable = new ProductTable(this.connection);
		this.productVersionTable = new ProductVersionTable(this.connection);
		this.productUserReviewTable = new ProductUserReviewTable(this.connection);

		success.set(true);
	}

	/**
	 * Destructor, close sql connection
	 */
	@Override
	protected void finalize() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		}
		try {
			super.finalize();
		} catch (Throwable e) {
			logger.error("Error on finalize", e);
		}
	}

	/**
	 * Checks if the product is in the database
	 *
	 * @param pid product set id
	 * @return true if exists
	 */
	public boolean hasProduct(int pid) {
		try {
			return this.productTable.hasProduct(pid);
		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		}
		return false;
	}

	/**
	 * Adds or edits a product to the database
	 *
	 * @param pid product set id
	 * @param oid ontology id
	 * @param editors_review_name string
	 * @param editors_review_date date
	 * @param editors_review_description string
	 * @param editors_review_text string
	 * @param editors_review_rating double
	 * @param users_review_rating double
	 * @param users_review_rating_count integer
	 * @param publisher_description string
	 * @param publisher_description_alterations string
	 * @param publisher_name string
	 * @param publisher_url string
	 * @param platform string
	 * @param category string
	 * @param subcategory string
	 * @param latest_id_v the latest version of the product
	 */
	public void updateProduct(int pid, int oid, String editors_review_name, Date editors_review_date,
	                          String editors_review_description, String editors_review_text,
	                          double editors_review_rating, double users_review_rating, int users_review_rating_count,
	                          String publisher_description, String publisher_description_alterations,
	                          String publisher_name, String publisher_url, String platform, String category,
	                          String subcategory, int latest_id_v) {

		java.sql.Date sql_editors_review_date = null;

		if (editors_review_date != null) {
			sql_editors_review_date = new java.sql.Date(editors_review_date.getTime());
		}

		try {
			this.productTable.updateProduct(pid, oid, editors_review_name, sql_editors_review_date,
					editors_review_description, editors_review_text, editors_review_rating, users_review_rating,
					users_review_rating_count, publisher_description, publisher_description_alterations,
					publisher_name, publisher_url, platform, category, subcategory, latest_id_v);
		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		}
	}

	/**
	 * Checks if the product version of the product set is in the database
	 *
	 * @param id_p product set id
	 * @param vid product version id
	 * @return true if exists
	 */
	public boolean hasProductVersion(int id_p, int vid) {
		try {
			return this.productVersionTable.hasProductVersion(id_p, vid);
		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		}
		return false;
	}

	/**
	 * Returns the product set id from the product version id
	 *
	 * @param vid product version id
	 * @return returns the product set id or 0
	 */
	public int getProductIDFromVersionID(int vid) {
		try {
			return this.productVersionTable.getProductIDFromVersionID(vid);
		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		}
		return 0;
	}

	/**
	 * Adds or edits a product version to the database
	 *
	 * @param id_p product set id
	 * @param vid product version id
	 * @param version_name string
	 * @param version_alterations string
	 * @param version_publish_date date
	 * @param version_added_date string
	 * @param version_identifier string
	 * @param operating_systems string
	 * @param additional_requirements string
	 * @param download_size string
	 * @param download_name string
	 * @param download_link string
	 * @param downloads_total integer
	 * @param downloads_last_week integer
	 * @param license_model string
	 * @param license_limitations string
	 * @param license_cost string
	 */
	public void updateProductVersion(int id_p, int vid, String version_name, String version_alterations, Date
			version_publish_date, Date version_added_date, String version_identifier, String operating_systems, String
			                                 additional_requirements, String download_size, String download_name, String download_link, int
			                                 downloads_total, int downloads_last_week, String license_model, String license_limitations, String
			                                 license_cost) {

		java.sql.Date sql_version_publish_date = null;
		java.sql.Date sql_version_added_date = null;

		if (version_publish_date != null) {
			sql_version_publish_date = new java.sql.Date(version_publish_date.getTime());
		}
		if (version_added_date != null) {
			sql_version_added_date = new java.sql.Date(version_added_date.getTime());
		}

		try {
			this.productVersionTable.updateProductVersion(id_p, vid, version_name, version_alterations,
					sql_version_publish_date, sql_version_added_date, version_identifier, operating_systems,
					additional_requirements, download_size, download_name, download_link, downloads_total,
					downloads_last_week, license_model, license_limitations, license_cost);
		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		}
	}

	/**
	 * Checks if the user review for the product version of a product set is in the database
	 *
	 * @param mid message id
	 * @param id_p product set id
	 * @param id_v product version id
	 * @return true if exists
	 */
	public boolean hasProductUserReview(int mid, int id_p, int id_v) {
		try {
			return this.productUserReviewTable.hasProductUserReview(mid, id_p, id_v);
		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		}
		return false;
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
	public void updateProductUserReview(int mid, int id_p, int id_v, double rating, String title, String author, Date
			date, String pros, String cons, String summary, int thumbs_up, int thumbs_down) {

		java.sql.Date sql_date = null;

		if (date != null) {
			sql_date = new java.sql.Date(date.getTime());
		}

		try {
			this.productUserReviewTable.updateProductUserReview(mid, id_p, id_v, rating, title, author, sql_date,
					pros, cons, summary, thumbs_up, thumbs_down);
		} catch (SQLException e) {
			DatabaseConnection.printSQLException(e);
		}
	}
}
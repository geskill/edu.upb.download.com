package edu.upb.winfo.downloadcom;

import edu.upb.winfo.utils.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by geskill on 02.01.2016.
 *
 * @author geskill
 */
public class LocalDatabase implements DatabaseInterface {

	private Connection connection = null;
	private ProductTable productTable = null;
	private ProductVersionTable productVersionTable = null;
	private ProductUserReviewTable productUserReviewTable = null;

	/**
	 *
	 * @param propertiesFileName
	 */
	public LocalDatabase(String propertiesFileName) {
		DatabaseConnection databaseConnection = null;
		try {
			databaseConnection = new DatabaseConnection(propertiesFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assert databaseConnection != null;
		try {
			this.connection = databaseConnection.getConnectionToDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.productTable = new ProductTable(this.connection);
		this.productVersionTable = new ProductVersionTable(this.connection);
		this.productUserReviewTable = new ProductUserReviewTable(this.connection);
	}

	/**
	 *
	 */
	@Override
	protected void finalize() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			super.finalize();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	/**
	 * Checks if the product set is already in the database
	 *
	 * @param pid product set id
	 * @return true if exists
	 */
	public boolean hasProduct(int pid) {
		try {
			return this.productTable.hasProduct(pid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param pid
	 * @param oid
	 * @param editors_review_name
	 * @param editors_review_date
	 * @param editors_review_description
	 * @param editors_review_text
	 * @param editors_review_rating
	 * @param users_review_rating
	 * @param users_review_rating_count
	 * @param publisher_description
	 * @param publisher_description_alterations
	 * @param publisher_name
	 * @param publisher_url
	 * @param platform
	 * @param category
	 * @param subcategory
	 * @param latest_id_v
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
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the product version of the product set is already in the database
	 *
	 * @param id_p
	 * @param vid
	 * @return true if exists
	 */
	public boolean hasProductVersion(int id_p, int vid) {
		try {
			return this.productVersionTable.hasProductVersion(id_p, vid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param id_p
	 * @param vid
	 * @param version_name
	 * @param version_alterations
	 * @param version_publish_date
	 * @param version_added_date
	 * @param version_identifier
	 * @param operating_systems
	 * @param additional_requirements
	 * @param download_size
	 * @param download_name
	 * @param download_link
	 * @param downloads_total
	 * @param downloads_last_week
	 * @param license_model
	 * @param license_limitations
	 * @param license_cost
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
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the user review for the product version of a product set is already in the database
	 *
	 * @param mid
	 * @param id_p
	 * @param id_v
	 * @return true if exists
	 */
	public boolean hasProductUserReview(int mid, int id_p, int id_v) {
		try {
			return this.productUserReviewTable.hasProductUserReview(mid, id_p, id_v);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param mid
	 * @param id_p
	 * @param id_v
	 * @param rating
	 * @param title
	 * @param author
	 * @param date
	 * @param pros
	 * @param cons
	 * @param summary
	 * @param thumbs_up
	 * @param thumbs_down
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
			e.printStackTrace();
		}
	}
}
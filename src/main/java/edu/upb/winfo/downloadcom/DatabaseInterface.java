package edu.upb.winfo.downloadcom;

import java.util.Date;

/**
 * Created by geskill on 02.01.2016.
 *
 * @author geskill
 */
public interface DatabaseInterface {

	/**
	 * Checks if the product is in the database
	 *
	 * @param pid product set id
	 * @return true if exists
	 */
	boolean hasProduct(int pid);

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
	void updateProduct(int pid, int oid, String editors_review_name, Date editors_review_date,
	                   String editors_review_description, String editors_review_text, double editors_review_rating,
	                   double users_review_rating, int users_review_rating_count, String publisher_description,
	                   String publisher_description_alterations, String publisher_name,
	                   String publisher_url, String platform, String category,
	                   String subcategory, int latest_id_v);

	/**
	 * Checks if the product version of the product set is in the database
	 *
	 * @param id_p product set id
	 * @param vid product version id
	 * @return true if exists
	 */
	boolean hasProductVersion(int id_p, int vid);

	/**
	 * Returns the product set id from the product version id
	 *
	 * @param vid product version id
	 * @return returns the product set id or 0
	 */
	int getProductIDFromVersionID(int vid);

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
	void updateProductVersion(int id_p, int vid, String version_name, String version_alterations,
	                          Date version_publish_date, Date version_added_date, String version_identifier,
	                          String operating_systems, String additional_requirements, String download_size,
	                          String download_name, String download_link, int downloads_total,
	                          int downloads_last_week, String license_model, String license_limitations,
	                          String license_cost);

	/**
	 * Checks if the user review for the product version of a product set is in the database
	 *
	 * @param mid message id
	 * @param id_p product set id
	 * @param id_v product version id
	 * @return true if exists
	 */
	boolean hasProductUserReview(int mid, int id_p, int id_v);

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
	void updateProductUserReview(int mid, int id_p, int id_v, double rating, String title, String author,
	                             Date date, String pros, String cons, String summary, int thumbs_up,
	                             int thumbs_down);

}

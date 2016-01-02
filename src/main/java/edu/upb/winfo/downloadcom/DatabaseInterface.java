package edu.upb.winfo.downloadcom;

import java.util.Date;

/**
 * Created by geskill on 02.01.2016.
 *
 * @author geskill
 */
public interface DatabaseInterface {

	/**
	 * Checks if the product set is already in the database
	 * @param pid product set id
	 * @return true if exists
	 */
	boolean hasProduct(int pid);

	/**
	 *
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
	void updateProduct(int pid, int oid, String editors_review_name, Date editors_review_date,
	                   String editors_review_description, String editors_review_text, double editors_review_rating,
	                   double users_review_rating, int users_review_rating_count, String publisher_description,
	                   String publisher_description_alterations, String publisher_name,
	                   String publisher_url, String platform, String category,
	                   String subcategory, int latest_id_v);

	/**
	 * Checks if the product version of the product set is already in the database
	 * @param id_p
	 * @param vid
	 * @return true if exists
	 */
	boolean hasProductVersion(int id_p, int vid);

	/**
	 *
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
	void updateProductVersion(int id_p, int vid, String version_name, String version_alterations,
	                          Date version_publish_date, Date version_added_date, String version_identifier,
	                          String operating_systems, String additional_requirements, String download_size,
	                          String download_name, String download_link, int downloads_total,
	                          int downloads_last_week, String license_model, String license_limitations,
	                          String license_cost);

	/**
	 * Checks if the user review for the product version of a product set is already in the database
	 * @param mid
	 * @param id_p
	 * @param id_v
	 * @return true if exists
	 */
	boolean hasProductUserReview(int mid, int id_p, int id_v);

	/**
	 *
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
	void updateProductUserReview(int mid, int id_p, int id_v, double rating, String title, String author,
	                             Date date, String pros, String cons, String summary, int thumbs_up,
	                             int thumbs_down);

}

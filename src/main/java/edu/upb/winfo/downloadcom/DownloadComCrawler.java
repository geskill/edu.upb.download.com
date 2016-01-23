package edu.upb.winfo.downloadcom;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.upb.winfo.utils.DateParser;
import edu.upb.winfo.utils.RegEx;
import org.apache.http.HttpStatus;

import java.util.regex.Matcher;

/**
 * Created by geskill on 30.12.2015.
 *
 * @author geskill
 */
public class DownloadComCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|gif|gz|jpg|js|mp3|png|xml|zip))$");

	/**
	 * This method receives two parameters. The first parameter is the page
	 * in which we have discovered this new url and the second parameter is
	 * the new url. You should implement this function to specify whether
	 * the given url should be crawled or not (based on your crawling logic).
	 * In this example, we are instructing the crawler to ignore urls that
	 * have css, js, git, ... extensions and to only accept urls that start
	 * with "http://download.cnet.com/". In this case, we didn't need the
	 * referringPage parameter to make the decision.
	 *
	 * The URLs of download.cnet.com can have two types. First they have a
	 * version name and some called "oid" (do not confuse this with ontology id).
	 *
	 *      |    |cat id|product set id (current version) or product version (older version)
	 * oid = 3000-2239_4-10019223
	 *
	 * The 3000 seems to be static, maybe this is the platform id, to deliver
	 * a different view for mobile devices. (don't know)
	 *
	 * The 2239_4 is the ontology id, better say the 2239. The _4 is again some
	 * static thing.
	 *
	 * The 10019223 can be the product set id (if this is the current version) or
	 * this is the product version id (if this is an older version).
	 *
	 * http://download.cnet.com/Avast-Free-Antivirus-2016/3000-2239_4-10019223.html
	 * http://download.cnet.com/archive/3000-2239_4-14477201.html
	 *
	 * The second url version seems to be for special partners of download.cnet.com (guess).
	 * This url is shorter and has no ids.
	 *
	 * http://download.cnet.com/ccleaner/
	 * http://download.cnet.com/archive/3000-18512_4-14488690.html
	 *
	 * Furthermore for all not current version there is a special /archive/ url.

	 * The user review urls have the following format:
	 *
	 *                   static       descending true/false
	 *  product   version  all versions?           page
	 * | set id |    id  |  |     |    sort   |    | | nodeId
	 * /10019223-14488453-10-false-createdDate-true-2-2239
	 *
	 * http://download.cnet.com/module/userReviews/10019223-14488453-10-false-createdDate-true-2-2239
	 * http://download.cnet.com/module/userReviews/10019223-14488453-10-false-createdDate-true-3-2239
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() && href.startsWith("http://download.cnet.com/")
				&& !href.contains("?")
				&& !href.startsWith("http://download.cnet.com/s/") // don't search in the search
				&& !href.startsWith("http://download.cnet.com/blog/") // don't search in the blog
				&& !href.startsWith("http://download.cnet.com/security-center/"); // don't search in the special security center area

		// TODO: Remote database should check if the entry exists (if possible [exception i.e. /ccleaner/])
	}

	/**
	 * This function is called once the header of a page is fetched. It can be
	 * overridden by sub-classes to perform custom logic for different status
	 * codes. For example, 404 pages can be logged, etc.
	 *
	 * @param webUrl WebUrl containing the statusCode
	 * @param statusCode Html Status Code number
	 * @param statusDescription Html Status COde description
	 */
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {

		super.handlePageStatusCode(webUrl, statusCode, statusDescription);

		if (statusCode != HttpStatus.SC_OK) {

			if ((statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) || (statusCode == HttpStatus.SC_BAD_GATEWAY)) {

				this.handleProblematicURL(webUrl);
			}
		}
	}

	/**
	 * This function is called if the content of a url could not be fetched.
	 *
	 * @param webUrl URL which content failed to be fetched
	 */
	@Override
	protected void onContentFetchError(WebURL webUrl) {

		super.onContentFetchError(webUrl);

		this.handleProblematicURL(webUrl);
	}

	/**
	 * This function is called when a unhandled exception was encountered during fetching
	 *
	 * @param webUrl URL where a unhandled exception occured
	 */
	protected void onUnhandledException(WebURL webUrl, Throwable e) {

		super.onUnhandledException(webUrl, e);

		this.handleProblematicURL(webUrl);
	}

	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 *
	 * We need to check if the visited page is a detailed product page
	 * or a comments only page. Otherwise we do nothing.
	 *
	 * @param page the page object that is just fetched and parsed.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		logger.info("URL: " + url);
		// System.out.println("URL: " + url);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();

			if (this.hasProduct(html)) {
				// This page is the detailed product page

				// Extract product information
				this.handleProduct(html, url);

				// Extract product version
				this.handleProductVersion(html);

				// Extract 10 comments present on the page
				this.handleUserReviews(html, url);

				// Check, whether there are more comments
				// belonging to the current fetched version
				this.handleFurtherUserReviews(html);
			} else {
				if (this.hasUserReviewsOnly(html)) {
					// This page is the comments only page

					// Extract 10 comments present on the page
					this.handleUserReviews(html, url);
				}
			}
		}
	}

	private static final String IS_PRODUCT_PAGE = "\"ptype\": \"product_detail\"";

	/**
	 * Evaluates if the web page is the product page using a
	 * unique string that is only present in a product page
	 *
	 * @param pageContent The HTML source code of the given page
	 * @return true if the given web page is a product page
	 */
	protected boolean hasProduct(String pageContent) {
		return pageContent.contains(IS_PRODUCT_PAGE);
	}

	/**
	 * Evaluates if the web page is the comments only page
	 * (with 10 comments, that is used by AJAX calls) using a
	 * string that indicates the start of the page
	 *
	 * @param pageContent The HTML source code of the given page
	 * @return true if the given web page is a comments only page
	 */
	protected boolean hasUserReviewsOnly(String pageContent) {
		return pageContent.trim().startsWith("<div class=\"paginationStrip header\">");
	}

	// Product

	private static final Pattern OIDString = Pattern.compile("data-oid=\"(.*?)\"");

	private static final Pattern PID = Pattern.compile("softwareProductId:\"(\\d+)\"");
	private static final Pattern OID = Pattern.compile("pageOntologyId:\"(\\d+)\"");

	private static final Pattern EDITORS_REVIEW_NAME = Pattern.compile("editor-review[\\s\\S]*?itemprop=\\\"name\\\">(.*?)<\\/span>");
	private static final Pattern EDITORS_REVIEW_DATE = Pattern.compile("editor-review[\\s\\S]*?<\\/a>\\s+on (.*?)\\s+<\\/");
	private static final Pattern EDITORS_REVIEW_DESCRIPTION = Pattern.compile("editor-review[\\s\\S]*?itemprop=\\\"description\\\".*?>\\s+<p>(.*?)<\\/");
	private static final Pattern EDITORS_REVIEW_TEXT = Pattern.compile("editor-review[\\s\\S]*?itemprop=\\\"description\\\"[\\s\\S]*?<\\/p>([\\s\\S]*?)<\\/div>");
	private static final Pattern EDITORS_REVIEW_RATING = Pattern.compile("editor-reviews-stars[\\s\\S]*?<span>([\\d\\.]+)<\\/span>");

	private static final Pattern USERS_REVIEW_RATING = Pattern.compile("user-reviews-stars[\\s\\S]*?\"ratingValue\">([\\d\\.]+)<\\/span>");
	// TODO: connect mobile rating + count?
	private static final Pattern USERS_REVIEW_RATING_COUNT = Pattern.compile("user-reviews-stars[\\s\\S]*?out of (\\d+) votes");

	private static final Pattern PUBLISHER_DESCRIPTION = Pattern.compile("publisher-description[\\s\\S]*?<\\/span>[\\s\\S]*?<\\/span>([\\s\\S]*?)<div");
	// INFO: some broken HTML at "See all new features"
	private static final Pattern PUBLISHER_DESCRIPTION_ALTERATIONS = Pattern.compile("publisher-description[\\s\\S]*?\"whatsnew\"[\\s\\S]*?<\\/p>([\\s\\S]*?)(<\\/div|<a)");

	private static final Pattern PUBLISHER_NAME = Pattern.compile("specsPubName[\\s\\S]*?Publisher[\\s\\S]*?>(.*?)<");
	private static final Pattern PUBLISHER_URL = Pattern.compile("specsPubName[\\s\\S]*?Publisher[\\s\\S]*?href=\"(.*?)\"");

	private static final Pattern PLATFORM = Pattern.compile("breadcrumb\"[\\s\\S]*?href=\\/.*?\\/\\?tag=bc>(.*?)<\\/a>");
	private static final Pattern PLATFORM_HIDDEN = Pattern.compile("breadcrumb\"[\\s\\S]*?href=\\/(\\w+)\\/\\w+");
	private static final Pattern CATEGORY = Pattern.compile("specsCategory[\\s\\S]*?Category[\\s\\S]*?\">(.*?)<");
	private static final Pattern SUBCATEGORY = Pattern.compile("specsSubcategory[\\s\\S]*?Subcategory[\\s\\S]*?\">(.*?)<");

	/**
	 * Handles the detailed product page and extracts product only details
	 * using the above specified regular expression patterns and saves the
	 * extracted content into the product database
	 *
	 * @param pageContent The HTML source code of the given page
	 * @param url The URL of the given page
	 */
	protected void handleProduct(String pageContent, String url) {

		int pid = Integer.parseInt(RegEx.getMatch(PID, pageContent));
		int oid = Integer.parseInt(RegEx.getMatch(OID, pageContent));

		// do not update product if already in the database
		if (App.DATABASE.hasProduct(pid) && url.contains("/archive/")) {
			return;
		}

		String editors_review_name = RegEx.getMatch(EDITORS_REVIEW_NAME, pageContent);
		Date editors_review_date = null;
		try {
			String string_editors_review_date = RegEx.getMatch(EDITORS_REVIEW_DATE, pageContent);
			if (!string_editors_review_date.isEmpty()) {
				editors_review_date = DateParser.getDate(string_editors_review_date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String editors_review_description = RegEx.getMatch(EDITORS_REVIEW_DESCRIPTION, pageContent);
		String editors_review_text = RegEx.getMatch(EDITORS_REVIEW_TEXT, pageContent);
		String string_editors_review_rating = RegEx.getMatch(EDITORS_REVIEW_RATING, pageContent);
		double editors_review_rating = 0;
		if (!string_editors_review_rating.isEmpty()) {
			editors_review_rating = Double.parseDouble(string_editors_review_rating);
		}
		String string_users_review_rating = RegEx.getMatch(USERS_REVIEW_RATING, pageContent);
		double users_review_rating = 0;
		if (!string_users_review_rating.isEmpty()) {
			users_review_rating = Double.parseDouble(string_users_review_rating);
		}
		String string_users_review_rating_count = RegEx.getMatch(USERS_REVIEW_RATING_COUNT, pageContent);
		int users_review_rating_count = 0;
		if (!string_users_review_rating_count.isEmpty()) {
			users_review_rating_count = Integer.parseInt(string_users_review_rating_count);
		}
		String publisher_description = RegEx.getMatch(PUBLISHER_DESCRIPTION, pageContent);
		String publisher_description_alterations = RegEx.getMatch(PUBLISHER_DESCRIPTION_ALTERATIONS, pageContent);
		String publisher_name = RegEx.getMatch(PUBLISHER_NAME, pageContent);
		String publisher_url = RegEx.getMatch(PUBLISHER_URL, pageContent);
		String platform = RegEx.getMatch(PLATFORM, pageContent);
		if (platform.isEmpty()) { // special cases
			platform = RegEx.getMatch(PLATFORM_HIDDEN, pageContent);
			if ("webware".equals(platform)) {
				platform = "Web Apps";
			} else if ("linux".equals(platform)) {
				platform = "Linux Software";
			}
		}
		String category = RegEx.getMatch(CATEGORY, pageContent);
		String subcategory = RegEx.getMatch(SUBCATEGORY, pageContent);

		int latest_id_v = -1;
		String oid_string = RegEx.getMatch(OIDString, pageContent);
		if (oid_string.contains(Integer.toString(pid))) {

			// problem if intentional wrong url is given: i.e. download.cnet.com/Avast-Free-Antivirus-2016/14488453
			// => data-oid="3000-2239_4-14488453" ... however only theoretically problematic

			// Newest version:
			// download.cnet.com/Avast-Free-Antivirus-2016/3000-2239_4-10019223.html
			// softwareProductId:"10019223"
			// data-oid="3000-2239_4-10019223"
			// softwareId:"14488453"

			// Any older version:
			// download.cnet.com/archive/3000-2239_4-14477201.html
			// softwareProductId:"10019223"
			// data-oid="3000-2239_4-14477201"
			// softwareId:"14477201"

			latest_id_v = Integer.parseInt(RegEx.getMatch(VID, pageContent));
		}

		App.DATABASE.updateProduct(pid, oid, editors_review_name, editors_review_date, editors_review_description,
				editors_review_text, editors_review_rating, users_review_rating, users_review_rating_count,
				publisher_description, publisher_description_alterations, publisher_name, publisher_url, platform,
				category, subcategory, latest_id_v);
	}

	// Product version

	private static final Pattern VID = Pattern.compile("softwareId:\"(\\d+)\"");
	private static final Pattern VERSION_NAME = Pattern.compile("softwareName:\"(.*?)\"");
	private static final Pattern VERSION_ALTERATIONS = Pattern.compile("full-specs[\\s\\S]*?tbody>([\\s\\S]*?)<\\/tbody>[\\s\\S]+specsPubName");

	private static final Pattern VERSION_PUBLISH_DATE = Pattern.compile("specsPubReleaseDate[\\s\\S]*?Date[\\s\\S]*?>(.*?)<");
	private static final Pattern VERSION_ADDED_DATE = Pattern.compile("specsPubDateAdded[\\s\\S]*?Added[\\s\\S]*?>(.*?)<");
	private static final Pattern VERSION_IDENTIFIER = Pattern.compile("specsPubVersion[\\s\\S]*?Version[\\s\\S]*?>(.*?)<");

	private static final Pattern OPERATING_SYSTEMS = Pattern.compile("specsOperatingSystem[\\s\\S]*?Systems[\\s\\S]*?<td>([\\s\\S]*?)<");
	private static final Pattern ADDITIONAL_REQUIREMENTS = Pattern.compile("specsRequirements[\\s\\S]*?Requirements[\\s\\S]*?<td>([\\s\\S]*?)<");

	private static final Pattern DOWNLOAD_SIZE = Pattern.compile("specsFileSize[\\s\\S]*?Size[\\s\\S]*?<td>([\\s\\S]*?)<");
	private static final Pattern DOWNLOAD_NAME = Pattern.compile("specsFileName[\\s\\S]*?Name[\\s\\S]*?<td>([\\s\\S]*?)<");
	private static final Pattern DOWNLOAD_LINK = Pattern.compile("data-dl-url='(.*?)\\?"); // INFO: the access token is not saved

	private static final Pattern DOWNLOADS_TOTAL = Pattern.compile("specsTotalDownload[\\s\\S]*?Downloads[\\s\\S]*?<td>([\\s\\S]*?)<");
	private static final Pattern DOWNLOADS_LAST_WEEK = Pattern.compile("specsDlsLastWeek[\\s\\S]*?Week[\\s\\S]*?<td>([\\s\\S]*?)<");

	private static final Pattern LICENSE_MODEL = Pattern.compile("specsLicenseModel[\\s\\S]*?Model[\\s\\S]*?<td>([\\s\\S]*?)<");
	private static final Pattern LICENSE_LIMITATIONS = Pattern.compile("specsLimitations[\\s\\S]*?Limitations[\\s\\S]*?<td>([\\s\\S]*?)<");
	private static final Pattern LICENSE_COST = Pattern.compile("specsPrice[\\s\\S]*?Price[\\s\\S]*?<td>([\\s\\S]*?)<");

	/**
	 * Handles the detailed product page and extracts version only details
	 * using the above specified regular expression patterns and saves the
	 * extracted content into the product version database
	 *
	 * @param pageContent The HTML source code of the given page
	 */
	protected void handleProductVersion(String pageContent) {

		int id_p = Integer.parseInt(RegEx.getMatch(PID, pageContent));
		int vid = Integer.parseInt(RegEx.getMatch(VID, pageContent));
		String version_name = RegEx.getMatch(VERSION_NAME, pageContent);
		String version_alterations = RegEx.getMatch(VERSION_ALTERATIONS, pageContent);
		Date version_publish_date = null;
		try {
			version_publish_date = DateParser.getDate(RegEx.getMatch(VERSION_PUBLISH_DATE, pageContent));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date version_added_date = null;
		try {
			version_added_date = DateParser.getDate(RegEx.getMatch(VERSION_ADDED_DATE, pageContent));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String version_identifier = RegEx.getMatch(VERSION_IDENTIFIER, pageContent);
		String operating_systems = RegEx.getMatch(OPERATING_SYSTEMS, pageContent);
		String additional_requirements = RegEx.getMatch(ADDITIONAL_REQUIREMENTS, pageContent);
		String download_size = RegEx.getMatch(DOWNLOAD_SIZE, pageContent);
		String download_name = RegEx.getMatch(DOWNLOAD_NAME, pageContent);
		String download_link = RegEx.getMatch(DOWNLOAD_LINK, pageContent);
		int downloads_total = 0;
		try {
			downloads_total = NumberFormat.getNumberInstance(Locale.US).parse(RegEx.getMatch(DOWNLOADS_TOTAL, pageContent)).intValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int downloads_last_week = 0;
		try {
			downloads_last_week = NumberFormat.getNumberInstance(Locale.US).parse(RegEx.getMatch(DOWNLOADS_LAST_WEEK, pageContent)).intValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String license_model = RegEx.getMatch(LICENSE_MODEL, pageContent);
		String license_limitations = RegEx.getMatch(LICENSE_LIMITATIONS, pageContent);
		String license_cost = RegEx.getMatch(LICENSE_COST, pageContent);

		App.DATABASE.updateProductVersion(id_p, vid, version_name, version_alterations, version_publish_date,
				version_added_date, version_identifier, operating_systems, additional_requirements, download_size,
				download_name, download_link, downloads_total, downloads_last_week, license_model,
				license_limitations, license_cost);
	}

	// User reviews

	private static final Pattern MESSAGE_CONTAINER = Pattern.compile("<div messageId([\\s\\S]*?)originalReview"); // add "messageId" at the result
	private static final Pattern MID = Pattern.compile("messageId=\"(\\d+)\"");
	private static final Pattern ID_P = PID;
	private static final Pattern ID_V = Pattern.compile("productId=\"(\\d+)\"");

	private static final Pattern RATING = Pattern.compile("<span>([\\d\\.]+) stars");
	private static final Pattern TITLE = Pattern.compile("\"title\">(.*?)<\\/");
	private static final Pattern AUTHOR = Pattern.compile("\"author\">[\\s\\S]*?\"> (.*?)<[\\s\\S]*?class=\"version\"");
	private static final Pattern DATE = Pattern.compile("\"author\">\\s+(.*?)\\s+(&nbsp;|<)");

	private static final Pattern PROS = Pattern.compile("Pros<\\/p>.*?\">([\\s\\S]*?)<\\/p>");
	private static final Pattern CONS = Pattern.compile("Cons<\\/p>.*?\">([\\s\\S]*?)<\\/p>");
	private static final Pattern SUMMARY = Pattern.compile("Summary<\\/p>.*?\">([\\s\\S]*?)<\\/p>");

	private static final Pattern THUMBS_UP = Pattern.compile("rating=1&[\\s\\S]*?\\((\\d+)\\)");
	private static final Pattern THUMBS_DOWN = Pattern.compile("rating=0&[\\s\\S]*?\\((\\d+)\\)");

	private static final Pattern ID_P_FROM_URL = Pattern.compile("userReviews\\/(\\d+)-");

	/**
	 * Handles the the user reviews from either the detailed product or
	 * the comments only page using the above specified regular expression
	 * patterns and saves the extracted content into the user comments database.
	 *
	 * The URL parameter is used for the comments only page, because:
	 * The detailed product page with the comments section has stored the
	 * product set id, but however the comments only page not. Therefore,
	 * the ID would be -1. Therefore, we try to retrieve the product set id
	 * from the database, again there might be a problem if we fetch a comment
	 * belonging to a version, which is not yet stored inside the database.
	 * The database will give us 0 as result and we then extract the product
	 * set id from the given url.
	 *
	 * @param pageContent The HTML source code of the given page
	 * @param url The URL of the given page
	 */
	protected void handleUserReviews(String pageContent, String url) {

		Matcher m = MESSAGE_CONTAINER.matcher(pageContent);
		while(m.find()) {

			String message = "messageId" + m.group(1);

			int mid = Integer.parseInt(RegEx.getMatch(MID, message));
			String string_id_p = RegEx.getMatch(ID_P, pageContent);
			int id_p = -1;
			if (!string_id_p.isEmpty()) {
				id_p = Integer.parseInt(string_id_p);
			}
			int id_v = Integer.parseInt(RegEx.getMatch(ID_V, message));
			if (id_p == -1) {
				// single user review page has no product set id
				id_p = App.DATABASE.getProductIDFromVersionID(id_v);
			}
			if ((id_p == 0) && (url.contains("/userReviews/"))) {
				// extract product set id from URL
				string_id_p = RegEx.getMatch(ID_P_FROM_URL, url);
				if (!string_id_p.isEmpty()) {
					id_p = Integer.parseInt(string_id_p);
				}
			}
			double rating = Double.parseDouble(RegEx.getMatch(RATING, message));
			String title = RegEx.getMatch(TITLE, message);
			String author = RegEx.getMatch(AUTHOR, message);
			Date date = null;
			try {
				date = DateParser.getDate(RegEx.getMatch(DATE, message));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String pros = RegEx.getMatch(PROS, message);
			String cons = RegEx.getMatch(CONS, message);
			String summary = RegEx.getMatch(SUMMARY, message);
			int thumbs_up = Integer.parseInt(RegEx.getMatch(THUMBS_UP, message));
			int thumbs_down = Integer.parseInt(RegEx.getMatch(THUMBS_DOWN, message));

			App.DATABASE.updateProductUserReview(mid, id_p, id_v, rating, title, author, date, pros, cons, summary,
					thumbs_up, thumbs_down);
		}
	}

	// Further user reviews

	private static final Pattern USER_REVIEW_LINK_CONTAINER = Pattern.compile("data-review-infinite-scroll-options='(.*?)'");
	private static final Pattern USER_REVIEW_LINK_MAX_PAGE = Pattern.compile("data-review-infinite-scroll-options[\\s\\S]*?<li>[\\s\\S]+data-pageNum=\"(\\d+)\"[\\s\\S]*?<li class=\"next\"");

	/**
	 * Determines if the given detail page offers more user reviews belonging to
	 * the current version than are actually shown (only 10 comments per page are
	 * shown to the user). In this case, this function generates seeds for the crawler
	 * to fetch the user reviews belonging to the current version in the future
	 *
	 * @param pageContent The HTML source code of the given page
	 */
	protected void handleFurtherUserReviews(String pageContent) {

		// do not retrieve further user review pages from other than current page version
		if (pageContent.contains("selected >All versions</option>")) {
			return;
		}

		String user_review_link_container = RegEx.getMatch(USER_REVIEW_LINK_CONTAINER, pageContent);
		Gson gson = new Gson();
		UserReviewURL userReviewURL = gson.fromJson(user_review_link_container, UserReviewURL.class);

		String string_user_review_link_max_page = RegEx.getMatch(USER_REVIEW_LINK_MAX_PAGE, pageContent);
		int user_review_link_max_page = 0;
		if (!string_user_review_link_max_page.isEmpty()) {
			user_review_link_max_page = Integer.parseInt(string_user_review_link_max_page);
		}

		if (user_review_link_max_page > 1) {

			// http://download.cnet.com/module/userReviews/10019223-14488453-10-false-createdDate-true-2-2239
			// user_review_link_max_page 5: 2,3,4,5

			for (int i = 1; i < user_review_link_max_page; i++) {

				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("http://download.cnet.com");
				stringBuilder.append(userReviewURL.xhrBaseRequestUrl);
				stringBuilder.append(i + 1);
				stringBuilder.append("-");
				stringBuilder.append(userReviewURL.nodeId);

				// http://download.cnet.com/module/userReviews/xhr/10712203-11021691-10-false-createdDate-true-2-2648
				// http://download.cnet.com/module/userReviews/10712203-11021691-10-false-createdDate-true-2-2648

				String newSeed = stringBuilder.toString().replace("/xhr/", "/");

				logger.info("ADD COMMENTS SEED: " + newSeed);
				// System.out.println("ADD COMMENTS SEED: " + newSeed);
				this.getMyController().addSeed(newSeed);
			}
		}

	}

	private static final int MAX_BAD_URL_RETRY = 3;

	private Hashtable<String, Integer> problematicURLs = new Hashtable<String, Integer>();

	/**
	 * All pages being skipped because of server problems (thus of 500 and 502)
	 * are re-inserted into the query
	 *
	 * However, sometimes the crawler fetches URLs that are really not reachable
	 * for this reason the problematicURls Hashtable is used. Every problematic
	 * URL will be inserted in this table. Every time the same problematic URL
	 * will be processed in this procedure, the count of this URL will be
	 * increased until the count is greater than MAX_BAD_URL_RETRY. After 3
	 * retries the URL will be skipped finally.
	 */
	protected void handleProblematicURL(WebURL webUrl) {

		String canonicalUrl = URLCanonicalizer.getCanonicalURL(webUrl.getURL());
		if (this.problematicURLs.containsKey(canonicalUrl)) {

			int retryCount = this.problematicURLs.get(canonicalUrl);

			if (retryCount >= MAX_BAD_URL_RETRY) {
				logger.info("FINALLY SKIPPING: " + webUrl.getURL());
				return; // Skip the schedule insertion
			} else {
				this.problematicURLs.put(canonicalUrl, retryCount + 1);
			}
		} else {
			this.problematicURLs.put(canonicalUrl, 1);
		}

		// re-insert problematic URL (prevent skipping using Frontier directly)
		this.getMyController().getFrontier().schedule(webUrl);
	}
}
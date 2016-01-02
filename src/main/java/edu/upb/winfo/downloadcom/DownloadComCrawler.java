/**
 *
 */
package edu.upb.winfo.downloadcom;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.upb.winfo.utils.DateParser;
import edu.upb.winfo.utils.RegEx;

import java.util.regex.Matcher;

/**
 * Created by geskill on 30.12.2015.
 *
 * @author geskill
 */
public class DownloadComCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
			+ "|png|mp3|mp3|zip|gz))$");

	/**
	 * This method receives two parameters. The first parameter is the page
	 * in which we have discovered this new url and the second parameter is
	 * the new url. You should implement this function to specify whether
	 * the given url should be crawled or not (based on your crawling logic).
	 * In this example, we are instructing the crawler to ignore urls that
	 * have css, js, git, ... extensions and to only accept urls that start
	 * with "http://www.ics.uci.edu/". In this case, we didn't need the
	 * referringPage parameter to make the decision.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
				&& href.startsWith("http://download.cnet.com/");
	}

	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());
		}
	}

	// Product

	private static final Pattern PID = Pattern.compile("softwareProductId:\"(\\d+)\"");
	private static final Pattern OID = Pattern.compile("pageOntologyId:\"(\\d+)\"");

	private static final Pattern EDITORS_REVIEW_NAME = Pattern.compile("editor-review[\\s\\S]*?itemprop=\\\"name\\\">(.*?)<\\/span>");
	private static final Pattern EDITORS_REVIEW_DATE = Pattern.compile("editor-review[\\s\\S]*?<\\/a>\\s+on (.*?)\\s+<\\/");
	private static final Pattern EDITORS_REVIEW_DESCRIPTION = Pattern.compile("editor-review[\\s\\S]*?itemprop=\\\"description\\\".*?>\\s+<p>(.*?)<\\/");
	private static final Pattern EDITORS_REVIEW_TEXT = Pattern.compile("editor-review[\\s\\S]*?itemprop=\\\"description\\\"[\\s\\S]*?<\\/p><p>([\\s\\S]*?)<\\/div>");
	private static final Pattern EDITORS_REVIEW_RATING = Pattern.compile("editor-reviews-stars[\\s\\S]*?<span>([\\d\\.]+)<\\/span>");

	private static final Pattern USERS_REVIEW_RATING = Pattern.compile("user-reviews-stars[\\s\\S]*?\"ratingValue\">([\\d\\.]+)<\\/span>");
	// TODO: connect mobile rating + count?
	private static final Pattern USERS_REVIEW_RATING_COUNT = Pattern.compile("user-reviews-stars[\\s\\S]*?out of (\\d+) votes");

	private static final Pattern PUBLISHER_DESCRIPTION = Pattern.compile("publisher-description[\\s\\S]*?<\\/span>[\\s\\S]*?<\\/span>([\\s\\S]*?)<div");
	private static final Pattern PUBLISHER_NAME = Pattern.compile("specsPubName[\\s\\S]*?Publisher[\\s\\S]*?>(.*?)<");
	private static final Pattern PUBLISHER_URL = Pattern.compile("specsPubName[\\s\\S]*?Publisher[\\s\\S]*?href=\"(.*?)\"");

	private static final Pattern PLATFORM = Pattern.compile("breadcrumb\"[\\s\\S]*?href=\\/.*?\\/\\?tag=bc>(.*?)<\\/a>");
	private static final Pattern CATEGORY = Pattern.compile("specsCategory[\\s\\S]*?Category[\\s\\S]*?\">(.*?)<");
	private static final Pattern SUBCATEGORY = Pattern.compile("specsSubcategory[\\s\\S]*?Subcategory[\\s\\S]*?\">(.*?)<");
	private static final Pattern LATEST_ID_V = Pattern.compile(""); // TODO: add

	public void handleProduct(String pageContent) {

		int pid = Integer.parseInt(RegEx.getMatch(PID, pageContent));
		int oid = Integer.parseInt(RegEx.getMatch(OID, pageContent));
		String editors_review_name = RegEx.getMatch(EDITORS_REVIEW_NAME, pageContent);
		try {
			Date editors_review_date = DateParser.getDate(RegEx.getMatch(EDITORS_REVIEW_DATE, pageContent));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String editors_review_description = RegEx.getMatch(EDITORS_REVIEW_DESCRIPTION, pageContent);
		String editors_review_text = RegEx.getMatch(EDITORS_REVIEW_TEXT, pageContent);
		double editors_review_rating = Double.parseDouble(RegEx.getMatch(EDITORS_REVIEW_RATING, pageContent));
		double users_review_rating = Double.parseDouble(RegEx.getMatch(USERS_REVIEW_RATING, pageContent));
		int users_review_rating_count = Integer.parseInt(RegEx.getMatch(USERS_REVIEW_RATING_COUNT, pageContent));
		String publisher_description = RegEx.getMatch(PUBLISHER_DESCRIPTION, pageContent);
		String publisher_name = RegEx.getMatch(PUBLISHER_NAME, pageContent);
		String publisher_url = RegEx.getMatch(PUBLISHER_URL, pageContent);
		String platform = RegEx.getMatch(PLATFORM, pageContent);
		String category = RegEx.getMatch(CATEGORY, pageContent);
		String subcategory = RegEx.getMatch(SUBCATEGORY, pageContent);
		int latest_id_v = Integer.parseInt(RegEx.getMatch(LATEST_ID_V, pageContent)); // TODO: compare

		// TODO: Add to database

		// TODO: Add seeds
	}

	// Product version

	private static final Pattern VID = Pattern.compile("softwareId:\"(\\d+)\"");
	private static final Pattern VERSION_NAME = Pattern.compile("softwareName:\"(.*?)\"");
	private static final Pattern VERSION_ALTERATIONS = Pattern.compile("full-specs[\\s\\S]*?tbody>([\\s\\S]*?)<\\/tbody>");

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

	public void handleProductVersion(String pageContent) {

		int id_p = Integer.parseInt(RegEx.getMatch(PID, pageContent));
		int vid = Integer.parseInt(RegEx.getMatch(VID, pageContent));
		String version_name = RegEx.getMatch(VERSION_NAME, pageContent);
		String version_alterations = RegEx.getMatch(VERSION_ALTERATIONS, pageContent);
		try {
			Date version_publish_date = DateParser.getDate(RegEx.getMatch(VERSION_PUBLISH_DATE, pageContent));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			Date version_added_date = DateParser.getDate(RegEx.getMatch(VERSION_ADDED_DATE, pageContent));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String version_identifier = RegEx.getMatch(VERSION_IDENTIFIER, pageContent);
		String operating_systems = RegEx.getMatch(OPERATING_SYSTEMS, pageContent);
		String additional_requirements = RegEx.getMatch(ADDITIONAL_REQUIREMENTS, pageContent);
		String download_size = RegEx.getMatch(DOWNLOAD_SIZE, pageContent);
		String download_name = RegEx.getMatch(DOWNLOAD_NAME, pageContent);
		String download_link = RegEx.getMatch(DOWNLOAD_LINK, pageContent);
		int downloads_total = Integer.parseInt(RegEx.getMatch(DOWNLOADS_TOTAL, pageContent));
		int downloads_last_week = Integer.parseInt(RegEx.getMatch(DOWNLOADS_LAST_WEEK, pageContent));
		String license_model = RegEx.getMatch(LICENSE_MODEL, pageContent);
		String license_limitations = RegEx.getMatch(LICENSE_LIMITATIONS, pageContent);
		String license_cost = RegEx.getMatch(LICENSE_COST, pageContent);

		// TODO: Add to database

		// TODO: Add seeds
	}

	// User reviews

	private static final Pattern MESSAGE_CONTAINER = Pattern.compile("<div messageId([\\s\\S]*?)originalReview"); // add "messageId" at the result
	private static final Pattern MID = Pattern.compile("messageId=\"(\\d+)\"");
	private static final Pattern ID_P = PID;
	private static final Pattern ID_V = Pattern.compile("productId=\"(\\d+)\"");

	private static final Pattern RATING = Pattern.compile("<span>([\\d\\.]+) stars");
	private static final Pattern TITLE = Pattern.compile("\"title\">(.*?)<\\/");
	private static final Pattern AUTHOR = Pattern.compile("\"author\">[\\s\\S]*?\"> (.*?)<");
	private static final Pattern DATE = Pattern.compile("\"author\">\\s+(.*?)\\s+&nbsp;");

	private static final Pattern PROS = Pattern.compile("Pros<\\/p>.*?\">([\\s\\S]*?)<\\/p>");
	private static final Pattern CONS = Pattern.compile("Cons<\\/p>.*?\">([\\s\\S]*?)<\\/p>");
	private static final Pattern SUMMARY = Pattern.compile("Summary<\\/p>.*?\">([\\s\\S]*?)<\\/p>");

	private static final Pattern THUMBS_UP = Pattern.compile("rating=1&[\\s\\S]*?\\((\\d+)\\)");
	private static final Pattern THUMBS_DOWN = Pattern.compile("rating=0&[\\s\\S]*?\\((\\d+)\\)");

	public void handleUserReviews(String pageContent) {

		Matcher m = MESSAGE_CONTAINER.matcher(pageContent);
		while(m.find()) {

			String message = m.group(1);

			int mid = Integer.parseInt(RegEx.getMatch(MID, message));
			int id_p = Integer.parseInt(RegEx.getMatch(ID_P, pageContent));
			int id_v = Integer.parseInt(RegEx.getMatch(ID_V, message));
			double rating = Double.parseDouble(RegEx.getMatch(RATING, message));
			String title = RegEx.getMatch(TITLE, message);
			String author = RegEx.getMatch(AUTHOR, message);
			try {
				Date date = DateParser.getDate(RegEx.getMatch(DATE, message));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String pros = RegEx.getMatch(PROS, message);
			String cons = RegEx.getMatch(CONS, message);
			String summary = RegEx.getMatch(SUMMARY, message);
			int thumbs_up = Integer.parseInt(RegEx.getMatch(THUMBS_UP, message));
			int thumbs_down = Integer.parseInt(RegEx.getMatch(THUMBS_DOWN, message));

			// TODO: Add to database
		}

		// TODO: Add seeds
	}

}

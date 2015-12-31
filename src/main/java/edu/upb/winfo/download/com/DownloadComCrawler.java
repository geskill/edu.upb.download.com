/**
 *
 */
package edu.upb.winfo.download.com;

import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
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

	private static final String PID = "softwareProductId:\"(\\d+)\"";
	private static final String OID = "pageOntologyId:\"(\\d+)\"";

	private static final String EDITORS_REVIEW_NAME = "editor-review[\\s\\S]*?itemprop=\\\"name\\\">(.*?)<\\/span>";
	private static final String EDITORS_REVIEW_DATE = "editor-review[\\s\\S]*?<\\/a>\\s+on (.*?)\\s+<\\/";
	private static final String EDITORS_REVIEW_DESCRIPTION = "editor-review[\\s\\S]*?itemprop=\\\"description\\\".*?>\\s+<p>(.*?)<\\/";
	private static final String EDITORS_REVIEW_TEXT = "editor-review[\\s\\S]*?itemprop=\\\"description\\\"[\\s\\S]*?<\\/p><p>([\\s\\S]*?)<\\/div>";
	private static final String EDITORS_REVIEW_RATING = "editor-reviews-stars[\\s\\S]*?<span>([\\d\\.]+)<\\/span>";

	private static final String USERS_REVIEW_RATING = "user-reviews-stars[\\s\\S]*?\"ratingValue\">([\\d\\.]+)<\\/span>";
	// TODO: connect mobile rating + count?
	private static final String USERS_REVIEW_RATING_COUNT = "user-reviews-stars[\\s\\S]*?out of (\\d+) votes";

	private static final String PUBLISHER_DESCRIPTION = "publisher-description[\\s\\S]*?<\\/span>[\\s\\S]*?<\\/span>([\\s\\S]*?)<div";
	private static final String PUBLISHER_NAME = "specsPubName[\\s\\S]*?Publisher[\\s\\S]*?>(.*?)<";
	private static final String PUBLISHER_URL = "specsPubName[\\s\\S]*?Publisher[\\s\\S]*?href=\"(.*?)\"";

	private static final String PLATFORM = "breadcrumb\"[\\s\\S]*?href=\\/.*?\\/\\?tag=bc>(.*?)<\\/a>";
	private static final String CATEGORY = "specsCategory[\\s\\S]*?Category[\\s\\S]*?\">(.*?)<";
	private static final String SUBCATEGORY = "specsSubcategory[\\s\\S]*?Subcategory[\\s\\S]*?\">(.*?)<";
	private static final String LATEST_ID_V = "";

	// Product version

	private static final String VID = "softwareId:\"(\\d+)\"";
	private static final String VERSION_NAME = "softwareName:\"(.*?)\"";
	private static final String VERSION_ALTERATIONS = "full-specs[\\s\\S]*?tbody>([\\s\\S]*?)<\\/tbody>";

	private static final String VERSION_PUBLISH_DATE = "specsPubReleaseDate[\\s\\S]*?Date[\\s\\S]*?>(.*?)<";
	private static final String VERSION_ADDED_DATE = "specsPubDateAdded[\\s\\S]*?Added[\\s\\S]*?>(.*?)<";
	private static final String VERSION_IDENTIFIER = "specsPubVersion[\\s\\S]*?Version[\\s\\S]*?>(.*?)<";

	private static final String OPERATING_SYSTEMS = "specsOperatingSystem[\\s\\S]*?Systems[\\s\\S]*?<td>([\\s\\S]*?)<";
	private static final String ADDITIONAL_REQUIREMENTS = "specsRequirements[\\s\\S]*?Requirements[\\s\\S]*?<td>([\\s\\S]*?)<";

	private static final String DOWNLOAD_SIZE = "specsFileSize[\\s\\S]*?Size[\\s\\S]*?<td>([\\s\\S]*?)<";
	private static final String DOWNLOAD_NAME = "specsFileName[\\s\\S]*?Name[\\s\\S]*?<td>([\\s\\S]*?)<";
	private static final String DOWNLOAD_LINK = "data-dl-url='(.*?)\\?"; // INFO: the access token is not saved

	private static final String DOWNLOADS_TOTAL = "specsTotalDownload[\\s\\S]*?Downloads[\\s\\S]*?<td>([\\s\\S]*?)<";
	private static final String DOWNLOADS_LAST_WEEK = "specsDlsLastWeek[\\s\\S]*?Week[\\s\\S]*?<td>([\\s\\S]*?)<";

	private static final String LICENSE_MODEL = "specsLicenseModel[\\s\\S]*?Model[\\s\\S]*?<td>([\\s\\S]*?)<";
	private static final String LICENSE_LIMITATIONS = "specsLimitations[\\s\\S]*?Limitations[\\s\\S]*?<td>([\\s\\S]*?)<";
	private static final String LICENSE_COST = "specsPrice[\\s\\S]*?Price[\\s\\S]*?<td>([\\s\\S]*?)<";

	public void visitMainPage(Page page) {

	}

	private static final String MESSAGE_CONTAINER = "<div messageId([\\s\\S]*?)originalReview"; // add "messageId" at the result
	private static final String MID = "messageId=\"(\\d+)\"";
	private static final String ID_P = PID;
	private static final String ID_V = "productId=\"(\\d+)\"";

	private static final String RATING = "<span>([\\d\\.]+) stars";
	private static final String TITLE = "\"title\">(.*?)<\\/";
	private static final String AUTHOR = "\"author\">[\\s\\S]*?\"> (.*?)<";
	private static final String DATE = "\"author\">\\s+(.*?)\\s+&nbsp;";

	private static final String PROS = "Pros<\\/p>.*?\">([\\s\\S]*?)<\\/p>";
	private static final String CONS = "Cons<\\/p>.*?\">([\\s\\S]*?)<\\/p>";
	private static final String SUMMARY = "Summary<\\/p>.*?\">([\\s\\S]*?)<\\/p>";

	private static final String THUMBS_UP = "rating=1&[\\s\\S]*?\\((\\d+)\\)";
	private static final String THUMBS_DOWN = "rating=0&[\\s\\S]*?\\((\\d+)\\)";

	public void visitCommentsPage(Page page) {

		// Pattern r = Pattern.compile(MESSAGE_CONTAINER);
	}

}

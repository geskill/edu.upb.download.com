package edu.upb.winfo.downloadcom;

import java.io.OutputStreamWriter;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import jd.http.RandomUserAgent;
import org.apache.log4j.*;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.varia.LevelMatchFilter;
import org.apache.log4j.varia.LevelRangeFilter;

/**
 * Created by geskill on 30.12.2015.
 *
 * crawler4j has some issues, that hinder the crawler to be more efficient
 * https://github.com/yasserg/crawler4j/issues/16
 *
 * @author geskill
 */
public class App {

	public static DatabaseInterface DATABASE = null;

	public static void main(String[] args) throws Exception {

		/*
         * Instantiate the application logging system.
         */
		Logger logger = Logger.getRootLogger();

		/*
         * Define log filter, to no log everything.
         */
		LevelRangeFilter filter = new LevelRangeFilter();
		filter.setAcceptOnMatch(true);
		filter.setLevelMin(Level.INFO);
		filter.setLevelMax(Level.FATAL);

		/*
         * Write log to console.
         */
		ConsoleAppender consoleAppender = new ConsoleAppender();
		consoleAppender.setName("ConsoleAppender");
		consoleAppender.setWriter(new OutputStreamWriter(System.out));
		consoleAppender.setLayout(new PatternLayout("%d{ISO8601}|%-6r [%t] %-5p %30.30c %x - %m%n"));
		consoleAppender.addFilter(filter);
		logger.addAppender(consoleAppender);

		/*
         * Write log to file.
         */
		FileAppender fileAppender = new FileAppender();
		fileAppender.setName("FileAppender");
		fileAppender.setFile("data/log/log.txt", true, false, 8*1024); // setFile(fileName) has bug
		fileAppender.setLayout(new PatternLayout("%d{ISO8601}|%-6r [%t] %-5p %30.30c %x - %m%n"));
		fileAppender.addFilter(filter);
		logger.addAppender(fileAppender);

		/*
         * Create now the database access.
         */
		DATABASE = new LocalDatabase("data/config/database.xml");

		/*
         * Instantiate the crawler configuration
         */
		String crawlStorageFolder = "data/crawl/root/";
		int numberOfCrawlers = 1; // TODO: rework on https://github.com/yasserg/crawler4j/issues/108

		CrawlConfig crawlConfig = new CrawlConfig();
		crawlConfig.setCrawlStorageFolder(crawlStorageFolder);
		crawlConfig.setResumableCrawling(true);
		crawlConfig.setUserAgentString(RandomUserAgent.generate());
		crawlConfig.setPolitenessDelay(10000);

        /*
         * Instantiate the controller for this crawl.
         */
		PageFetcher pageFetcher = new PageFetcher(crawlConfig);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		robotstxtConfig.setEnabled(false);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
		controller.addSeed("http://download.cnet.com/");
		controller.addSeed("http://download.cnet.com/windows/");
		controller.addSeed("http://download.cnet.com/mac/");
		controller.addSeed("http://download.cnet.com/webware/");
		controller.addSeed("http://download.cnet.com/ios/");
		controller.addSeed("http://download.cnet.com/android/");
		controller.addSeed("http://download.cnet.com/mobile/");

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
		controller.start(DownloadComCrawler.class, numberOfCrawlers);
	}
}

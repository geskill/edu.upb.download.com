package edu.upb.winfo.downloadcom;

import uk.org.lidalia.slf4jext.Logger;
import uk.org.lidalia.slf4jext.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by geskill on 10.01.2016.
 *
 * Simple Text IO reader/writer handling the seeds file
 *
 * @author geskill
 */
public class SeedReader {

	protected static final Logger logger = LoggerFactory.getLogger(SeedReader.class);

	private List<String> seedlist = new ArrayList<String>();

	/**
	 *
	 * @param fileName the file name of the seeds file
	 */
	public SeedReader(String fileName) {

		File file = new File(fileName);
		if (!file.exists()) {
			this.CreateDefaultSeedsFile(file);
		} else {
			this.ReadSeedsFile(file);
		}
	}

	/**
	 * Creates default seeds
	 */
	private void CreateDefaultSeeds() {

		this.seedlist.add("http://download.cnet.com/");
		this.seedlist.add("http://download.cnet.com/windows/");
		this.seedlist.add("http://download.cnet.com/mac/");
		this.seedlist.add("http://download.cnet.com/webware/");
		this.seedlist.add("http://download.cnet.com/ios/");
		this.seedlist.add("http://download.cnet.com/android/");
		this.seedlist.add("http://download.cnet.com/mobile/");
	}

	/**
	 * Creates default seeds file with the default seeds
	 * @param file the file of the seeds file
	 */
	private void CreateDefaultSeedsFile(File file) {

		this.CreateDefaultSeeds();

		BufferedWriter writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(file));

			for (String s : this.seedlist) {
				writer.write(s);
				writer.newLine();
			}

		} catch (Exception e) {
			logger.error("Error writing seeds file", e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
				logger.error("Error closing writing seeds file", e);
			}
		}
	}

	/**
	 * Reads the seeds from the seeds file. If file is empty default seeds are used
	 * @param file file the file of the seeds file
	 */
	private void ReadSeedsFile(File file) {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
				this.seedlist.add(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			logger.error("Error reading seeds file", e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				logger.error("Error losing reading seeds file", e);
			}
		}

		if (this.seedlist.isEmpty()) {
			this.CreateDefaultSeedsFile(file);
		}
	}

	/**
	 *
	 * @return list of seeds
	 */
	public List<String> getSeeds() {
		return this.seedlist;
	}
}

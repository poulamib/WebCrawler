package com.training.webcrawler;

import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class URLCrawler {

	/**
	 * This is the main method which will start the web crawler and also store
	 * the retrieved emails in a file.
	 * */
	public static void main(String[] args) throws IOException {

		processPage("http://mail-archives.apache.org/mod_mbox/maven-users/", 0);
		CrawlerUtil.storeEmailsInFile();
	}

	/**
	 * 
	 * @param url
	 *            - This is the web page that will be parsed to return the
	 *            required links
	 * @param depth
	 *            - This parameter determines how deep the crawler has gone into
	 *            the root web page
	 */
	public static void processPage(String url, int depth) throws IOException {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e1) {
			e1.printStackTrace();
			throw e1;
		}

		if (depth == 0) {
			/*Elements links = doc.select("a[href^=201502]").select(
					"a[href*=thread]");
			for (Element link : links) {
				processPage(link.attr("abs:href"), 1);
			}
*/		parseUrl(doc, "a[href^=201502]~a[href*=thread]", 0);
		} else if (depth == 1) {
			processPage(url, 2);
			LinkedList<String> absLinksList = new LinkedList<String>();
			Elements links = doc.select("a[href*=thread?]");
			for (Element link : links) {
				if (CrawlerUtil.checkIfExists(link.attr("abs:href"),
						absLinksList) == -1) {
					processPage(link.attr("abs:href"), 2);
					absLinksList.add(link.attr("abs:href"));
				}

			}
		} else if (depth == 2) {

			Elements links = doc.select("a[href*=@]");
			for (Element link : links) {
				processPage(link.attr("abs:href"), 3);

			}
		} else if (depth == 3) {

			Elements links = doc.getElementsByTag("pre");

			for (Element link : links) {
				String str = link.toString().replaceAll("&gt;", "")
						.replaceAll("&lt;", "");
				CrawlerUtil.addToEmailList(str);
			}

		}
	}

	public static void parseUrl(Document doc, String docSelector, int depth)
			throws IOException {
		Elements links = new Elements();
		String[] selectors = docSelector.split("~");
		for (String selector : selectors) {
			if (links.size() == 0)
				links = doc.select(selector);
			else
				links = links.select(selector);
			links = links.size() == 0 ? doc.select(selector) : links.select(selector);
		}
		for (Element link : links) {
			processPage(link.attr("abs:href"), depth + 1);
		}
	}

}

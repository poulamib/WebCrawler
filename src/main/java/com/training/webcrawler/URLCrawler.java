package com.training.webcrawler;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class URLCrawler
{

	/**
	 * This is the main method which will start the web crawler to download
	 * emails from 2015 and save them in a directory where email's from each
	 * month will be in a separate folder The main program deletes the
	 * EmailDirectory if it exists and creates a new one with the same name
	 * where the emails will be downloaded.
	 * 
	 * */
	public static void main(String[] args) throws IOException
	{
		System.out.println("******Web Crawler Started*******");
		// com.training.webcrawler.URLCrawler
		// "http://mail-archives.apache.org/mod_mbox/maven-users/"
		CrawlerUtil.setDirPath(args[1]);
		CrawlerUtil.deleteDirectory(new File(args[1])); // deleting the
														// existing dir
		new File(args[1]).mkdir();
		// creating a new dir
		processPage(args[0], 0);

		System.out.println("*******Web Crawler Completed Successfully*******");
	}

	/**
	 * This method is used for crawling the web page and parsing the urls
	 * 
	 * @param url
	 *            - This is the web page that will be parsed to return the
	 *            required links
	 * @param depth
	 *            - This parameter determines how deep the crawler has gone into
	 *            the root web page
	 */
	public static void processPage(String url, int depth) throws IOException
	{
		Document doc = null;
		try
		{
			doc = Jsoup.connect(url).get();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			throw e1;
		}
		if (depth == 0)
		{
			CrawlerUtil.parseUrl(doc, "a[href^=2015]~a[href*=thread]", depth);
		}
		else if (depth == 1)
		{
			processPage(url, 2);
			CrawlerUtil.doPagination(doc, "a[href*=thread?]", depth);
		}
		else if (depth == 2)
		{
			CrawlerUtil.parseUrl(doc, "a[href*=@]", depth);
		}
		else if (depth == 3)
		{
			CrawlerUtil.downloadEmails(doc);
		}
	}

}

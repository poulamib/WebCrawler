package com.training.webcrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerUtil
{
	/**
	 * the static variable dirpath is to maintain the location where the email's
	 * are to be downloaded
	 */
	private static String dirPath = new String();

	/**
	 * the static variable fileInc is a sequence that has been used in file
	 * naming
	 */
	private static int fileInc = 0;

	private static String folderName = "";

	public static String getDirPath()
	{
		return dirPath;
	}

	public static void setDirPath(String dirPath)
	{
		CrawlerUtil.dirPath = dirPath;
	}

	public static int getFileInc()
	{
		return fileInc;
	}

	public static void setFileInc(int fileInc)
	{
		CrawlerUtil.fileInc = fileInc;
	}

	public static String getFolderName()
	{
		return folderName;
	}

	public static void setFolderName(String folderName)
	{
		CrawlerUtil.folderName = folderName;
	}

	/**
	 * This method checks if the link has already been parsed.
	 * 
	 * @param link
	 *            This parameter will be checked against the list of links to
	 *            see if it already exists in the links
	 * @param links
	 *            This maintains the list of links that has already been parsed
	 *            by the web crawler
	 * @return
	 */
	public static int checkIfExists(String link, LinkedList<String> links)
	{
		int index = 0;
		index = links.indexOf(link);
		return index;
	}

	/**
	 * This method stores a single email in a file specified by the path
	 * CrawlerUtil.getDirPath()
	 * 
	 * @param str
	 * @throws IOException
	 */
	public static void storeEmailsInFile(String str) throws IOException
	{
		FileWriter fw;
		BufferedWriter bw;
		// System.out.println(CrawlerUtil.getDirPath());
		File file = new File(CrawlerUtil.getDirPath() + "/" + getFolderName()
				+ "/email-" + (fileInc++) + ".txt");
		if (!file.exists())
		{
			file.createNewFile();
		}

		fw = new FileWriter(file.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		try
		{
			bw.append(str);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			bw.close();
			fw.close();
		}

	}

	/**
	 * This method will parse the web page based on the selector specified in
	 * docSelector
	 * 
	 * @param doc
	 *            The web page document that needs to be parsed
	 * @param docSelector
	 *            The selector on the basis of which the web page will be parsed
	 * @param depth
	 *            This parameter determines how deep the crawler has gone into
	 *            the root web page
	 * @throws IOException
	 */
	public static void parseUrl(Document doc, String docSelector, int depth)
			throws IOException
	{
		Elements links = new Elements();
		String[] selectors = docSelector.split("~");
		for (String selector : selectors)
		{
			links = links.size() == 0 ? doc.select(selector) : links
					.select(selector);
		}
		for (Element link : links)
		{
			if (depth == 0)
			{
				// CrawlerUtil.setDirPath(createDirectory(link.attr("abs:href")));
				CrawlerUtil.createDirectory(link.attr("abs:href"));
				CrawlerUtil.setFileInc(0); // if new month create a folder for
											// that month
			}
			URLCrawler.processPage(link.attr("abs:href"), depth + 1);
		}
	}

	/**
	 * This method is used for parsing the web page that do not contain all the
	 * links in a single page and also checks if that web page has been parsed
	 * before
	 * 
	 * @param doc
	 *            The web page document that needs to be parsed
	 * @param depth
	 *            The selector on the basis of which the web page will be parsed
	 * @throws IOException
	 */
	public static void doPagination(Document doc, String docSelector, int depth)
			throws IOException
	{
		LinkedList<String> absLinksList = new LinkedList<String>();
		Elements links = doc.select(docSelector);
		for (Element link : links)
		{
			if (CrawlerUtil.checkIfExists(link.attr("abs:href"), absLinksList) == -1)
			{
				URLCrawler.processPage(link.attr("abs:href"), depth + 1);
				absLinksList.add(link.attr("abs:href"));
			}

		}
	}

	/**
	 * 
	 * @param doc
	 * @throws IOException
	 */
	public static void downloadEmails(Document doc) throws IOException
	{
		Elements links = doc.getElementsByTag("pre");
		for (Element link : links)
		{
			String str = link.toString().replaceAll("&gt;", "")
					.replaceAll("&lt;", "");
			storeEmailsInFile(str);
		}
	}

	/**
	 * To create a directory to store the emails
	 * 
	 * @param url
	 * @return
	 */
	public static String createDirectory(String url)
	{
		setFolderName(getDirectoryName(url));
		new File(getDirPath() + "/" + folderName).mkdir();
		return getDirPath() + "/" + folderName;

	}

	/**
	 * To get the directory name
	 * 
	 * @param url
	 * @return
	 */
	public static String getDirectoryName(String url)
	{
		int firstIndex = url.indexOf("maven-users/") + "maven-users/".length();
		int lastIndex = url.indexOf(".mbox");
		return url.substring(firstIndex, lastIndex);
	}

	/**
	 * 
	 * @param directory
	 * @return
	 */
	public static boolean deleteDirectory(File directory)
	{
		if (directory.exists())
		{
			File[] files = directory.listFiles();
			if (null != files)
			{
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].isDirectory())
					{
						deleteDirectory(files[i]);
					}
					else
					{
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}
}

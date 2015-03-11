package com.training.webcrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

public class CrawlerUtil {
	/**
	 *  The static variable emailsList is used to maintain the list of
	 *  emails that are required to be downloaded from the given web page
	 */
	public static LinkedList<String> emailsList = new LinkedList<String>();
	/**
	 * 
	 * @param link This parameter will be checked against the list of
	 * links to see if it already exists in the links
	 * @param links This maintains the list of links that has already been 
	 * parsed by the web crawler
	 * @return
	 */
	public static int checkIfExists(String link, LinkedList<String> links) {
		int index = 0;
		index = links.indexOf(link);
		return index;
	}
	
	/**
	 * This method adds the email content to a list of emails 
	 * @param str This parameter contains the email body
	 */
	public static void addToEmailList(String str) {
		emailsList.add(str);
	}

	/**
	 * This method will store all the emails contained in the emailList
	 * into a file
	 */
	public static void storeEmailsInFile() throws IOException{
		FileWriter fw ;
		BufferedWriter bw;
		
		File file = new File(
				"/home/poulamib/workspace/WebCrawlerTest/emails.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		fw = new FileWriter(file.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		try 
		{
			ListIterator<String> itr = emailsList.listIterator();
			while (itr.hasNext()) {
				bw.append(itr.next());
			}
			
			System.out.println("Done");
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			bw.close();
			fw.close();
		}

	}

}

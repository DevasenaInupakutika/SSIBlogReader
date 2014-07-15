package uk.software.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DOMParser {
	private RSSFeed _feed = new RSSFeed();
	private String htmlBlog = new String();

	public RSSFeed parseXml(String xml) {

		URL url = null;
		try {
			url = new URL(xml);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		try {
			// Create required instances
			DocumentBuilderFactory dbf;
			dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			// Parse the xml
			Document doc = db.parse(new InputSource(url.openStream()));
			doc.getDocumentElement().normalize();

			// Get all <item> tags.
			NodeList nl = doc.getElementsByTagName("item");
			int length = nl.getLength();

			for (int i = 0; i < 200; i++) {
				Node currentNode = nl.item(i);
				RSSItem _item = new RSSItem();

				NodeList nchild = currentNode.getChildNodes();
				int clength = nchild.getLength();

				// Get the required elements from each Item
				for (int j = 1; j < clength; j = j + 2) {

					Node thisNode = nchild.item(j);
					String theString = null;
					String nodeName = thisNode.getNodeName();

					theString = nchild.item(j).getFirstChild().getNodeValue();

					if (theString != null) {
						if ("title".equals(nodeName)) {
							// Node name is equals to 'title' so set the Node
							// value to the Title in the RSSItem.
							_item.setTitle(theString);
						}

						else if ("description".equals(nodeName)) {
							_item.setDescription(theString);

							// Parse the html description to get the image url
							String html = theString;
							org.jsoup.nodes.Document docHtml = Jsoup
									.parse(html);
							Elements imgEle = docHtml.select("img");
							
							if(imgEle.isEmpty()){
								imgEle = docHtml.select("iframe");
							    _item.setVideo(imgEle.attr("src"));
							}
						    else
							    _item.setImage(imgEle.attr("src"));
						}
						
						else if("link".equals(nodeName)){
							
							_item.setLink(theString);
						}

						else if ("pubDate".equals(nodeName)) {

							// We replace the plus and zero's in the date with
							// empty string
							String formatedDate = theString.replace(" +0000",
									"");
							_item.setDate(formatedDate);
						}

					}
				}

				// add item to the list
				_feed.addItem(_item);
			}

		} catch (Exception e) {
		}

		// Return the final feed once all the Items are added to the RSSFeed
		// Object(_feed).
		return _feed;
	}

    public String parseHtml(String link){
		
		URL blogURL = null;
		try {
			blogURL = new URL(link);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			InputStream is = (InputStream) blogURL.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			StringBuffer sb = new StringBuffer();
			
			while((line = br.readLine()) != null){
				   sb.append(line);
				 }
				 htmlBlog = sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return htmlBlog;
		
	}


}
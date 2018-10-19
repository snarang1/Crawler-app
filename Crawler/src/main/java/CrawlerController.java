package main.java;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ComponentScan
@RestController
public class CrawlerController {

	private final static String LINK_SELECTOR = "body a[href]";
	private final static String IMAGE_SELECTOR = "img";
	private static String domain;

	private static String host;

	 HashSet<String> pageLinksList = new HashSet<String>();
	 HashSet<String> outsideLinksList = new HashSet<String>();
	 HashSet<String> filesLinksList = new HashSet<String>();
	 HashSet<String> errorList = new HashSet<String>();
	 HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();

	
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, HashSet<String>> getInfo(@RequestBody String url) {
		result.clear();
		filesLinksList.clear();
		errorList.clear();
		outsideLinksList.clear();
		pageLinksList.clear();
		Boolean valid = testValidUrl(url);
		if (valid) {
			populateDomainHost(url);
			getLinksOnPage(url);
			return getResult();
		} else {
			errorList.add("Invalid Url");
			result.put("Error", errorList);
			
			return result;
		}

	}
	/** test to if the entered url is valid or not.
	 * @param url
	 */
	private Boolean testValidUrl(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/** populates domain in order to verify for internal addresses
	 * @param url
	 */
	private static void populateDomainHost(String url) {

		domain = url;
		try {
			host = new URI(CrawlerController.domain).getHost();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}
	/** get all the links to segregate according to their types
	 * @param url
	 */
	private void getLinksOnPage(String url) {
		String link;
		if (!pageLinksList.contains(url)) {

			try {
				Document document = Jsoup.connect(url).get();
				Elements linksOnPage = document.select(LINK_SELECTOR);
				Elements media = document.select(IMAGE_SELECTOR);

				for (Element element : linksOnPage) {
					url= normalizeUrl(element.attr("href"), domain);
					if (isExternalLink(url) && url.length() > 0) {
						addOutsideLinksList(url);
					} else {
						addPageLinksList(url);
					}
				}

				for (Element element : media) {
					url= normalizeUrl(element.attr("abs:src"), domain);
					if (url.length() > 0) {
						addFilesLinksList(url);
					}
				}

			} catch (IOException e) {
				System.err.println("For '" + url + "': " + e.getMessage());
			}
		}

	}

	/** populates static files
	 * @param url
	 */
	private void addFilesLinksList(String url) {
		// TODO Auto-generated method stub
		filesLinksList.add(url);

	}

	/** populates external links
	 * @param url
	 */
	private void addOutsideLinksList(String url) {
		// TODO Auto-generated method stub
		outsideLinksList.add(url);

	}

	/**populates internal links
	 * @param url
	 */
	private void addPageLinksList(String url) {
		// TODO Auto-generated method stub
		pageLinksList.add(url);
	}

	/**checks for external links
	 * @param url
	 * @return
	 */
	private boolean isExternalLink(String url) {

		if (url.equals("")) {
			return false;
		}

		URI uri;

		try {
			uri = new URI(url);
			if (Objects.equals(uri.getHost(), CrawlerController.host)) {
				return false;
			}
			return true;

		} catch (URISyntaxException e) {
		}

		return false;
	}

	/**
	 * @param url
	 * @param domain
	 * @return
	 */
	private String normalizeUrl(String url, String domain) {

		if (url.startsWith("mailto:")) {
			return "";
		}

		URI uri;
		try {

			uri = new URI(url);
			if (!uri.isAbsolute()) {
				uri = new URI(domain + "/").resolve(uri).normalize();
			}

			String uriStr = uri.toString();

			// remove fragment
			if (uri.getFragment() != null) {
				int index = uriStr.indexOf("#");
				if (index == -1) { // should't occur
					return uriStr;
				}
				return uriStr.substring(0, index);
			}

			return uriStr;

		} catch (URISyntaxException e) {
		}

		return "";
	}

	/**
	 * @return
	 */
	public  HashMap<String, HashSet<String>> getResult() {
		if (pageLinksList != null) {
			result.put("Internal Links", pageLinksList);
			
		}
		if (outsideLinksList != null) {
			result.put("outside Links", outsideLinksList);
			
		}
		if (filesLinksList != null) {
			result.put("files", filesLinksList);
			
		}
		return result;
	}
}

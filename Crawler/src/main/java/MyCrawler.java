package main.java;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class MyCrawler {
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

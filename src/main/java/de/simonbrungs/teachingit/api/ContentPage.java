package de.simonbrungs.teachingit.api;

public class ContentPage {
	private final String htmlContent;
	private final String websiteTitle;

	public ContentPage(String pHTMLContent, String pWebsiteTitle) {
		htmlContent = pHTMLContent;
		websiteTitle = pWebsiteTitle;
	}

	public String getHTMLContent() {
		return htmlContent;
	}

	public String getWebsiteTitle() {
		return websiteTitle;
	}
}

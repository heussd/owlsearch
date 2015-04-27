package com.github.heussd.owlsearch.object;

import org.rssowl.core.internal.persist.Description;
import org.rssowl.core.internal.persist.News;

import com.github.heussd.owlsearch.RssOwlInternals;

public class NewsItem {

	private float score;
	private String link;
	private String description;

	private String title;

	public NewsItem(News news, Description description, float score) {
		this.score = score;
		this.link = news.getLinkAsText();
		this.description = description.getValue();
		this.title = RssOwlInternals.getNewsTitle(news);

	}


	@Override
	public String toString() {
		return "NewsItem [score=" + score + ", link=" + link + ", description=" + description + ", title=" + title + "]";
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getTitle() {
		return title;
	}


}

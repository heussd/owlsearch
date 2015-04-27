package com.github.heussd.owlsearch;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.heussd.owlsearch.object.NewsItem;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedOutput;

public class OwlSearchHttpServlet extends HttpServlet {

	private OwlSearch owlSearch = null;
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/xml");

		try {
			response.getWriter().println(query(request.getParameter("q")));
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public String query(String q) throws Exception {

		if (owlSearch == null) {
			this.owlSearch = new OwlSearch();
			owlSearch.initialize();
		}

		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");

		feed.setTitle("Sample Feed (created with ROME)");
		feed.setLink("http://rome.dev.java.net");
		feed.setDescription("This feed has been created using ROME (Java syndication utilities");

		List<SyndEntry> entries = new ArrayList<SyndEntry>();

		for (NewsItem newsItem : owlSearch.search(q)) {
			SyndEntry entry;
			entry = new SyndEntryImpl();
			entry.setTitle(newsItem.getTitle());
			entry.setLink(newsItem.getLink());
			SyndContent description = new SyndContentImpl();
			description.setType("text/plain");
			description.setValue(newsItem.getDescription());
			entry.setDescription(description);

			// entry.setPublishedDate(DATE_PARSER.parse("2004-07-27"));
			entries.add(entry);
		}
		feed.setEntries(entries);
		StringWriter stringWriter = new StringWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(feed, stringWriter);

		return stringWriter.toString();
	}
}

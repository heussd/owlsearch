package com.github.heussd.owlsearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.rssowl.core.internal.persist.Description;
import org.rssowl.core.internal.persist.News;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.github.heussd.owlsearch.object.NewsItem;

public class OwlSearch {

	private String rssOwlFolder = System.getProperty("user.home") + "/.rssowl2/.metadata/.plugins/org.rssowl.core/";
	private FSDirectory fsDirectory;
	ObjectContainer db;

	public void initialize() throws Exception {
		db = Db4o.openFile(rssOwlFolder + "rssowl.db");
		db.ext().configure().readOnly(true);

		fsDirectory = FSDirectory.getDirectory(rssOwlFolder);
	}

	public List<NewsItem> search(String queryString) throws Exception {
		ArrayList<NewsItem> newsItems = new ArrayList<>();

		QueryParser queryParser = RssOwlInternals.getQueryParser();
		org.apache.lucene.search.Query query = queryParser.parse(queryString);

		final IndexSearcher indexSearcher = new IndexSearcher(fsDirectory);

		TopDocs topDocs = indexSearcher.search(query, null, 20);

		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);

			News news = RssOwlInternals.retrieveNews(db, document);
			Description description = RssOwlInternals.retrieveDescription(db, document);

			NewsItem newsItem = new NewsItem(news, description, scoreDoc.score);
			newsItems.add(newsItem);
		}
		return newsItems;
	}

}
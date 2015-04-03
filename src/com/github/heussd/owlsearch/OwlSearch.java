package com.github.heussd.owlsearch;

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

public class OwlSearch {

	public static void main(String[] args) throws Exception {

		String rssOwlFolder = "org.rssowl.core/";

		final ObjectContainer db = Db4o.openFile(rssOwlFolder + "/rssowl.db");
		db.ext().configure().readOnly(true);

		FSDirectory fsDirectory = FSDirectory.getDirectory(rssOwlFolder);

		QueryParser queryParser = RssOwlInternals.getQueryParser();
		org.apache.lucene.search.Query query = queryParser.parse("microsoft windows");

		final IndexSearcher indexSearcher = new IndexSearcher(fsDirectory);

		TopDocs topDocs = indexSearcher.search(query, null, 10);

		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);
			System.out.println(scoreDoc.score);

			News news = RssOwlInternals.retrieveNews(db, document);
			Description description = RssOwlInternals.retrieveDescription(db, document);

			System.out.println(news.getLinkAsText());
			System.out.println(description.getValue());
		}

	}

}
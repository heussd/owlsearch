package com.github.heussd.owlsearch;

import java.lang.reflect.Field;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.rssowl.core.internal.persist.Description;
import org.rssowl.core.internal.persist.News;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

/**
 * This class abstracts some details of RSSOwl's architecture, including object
 * retrieval from DB4o and Lucene search, in user-friendly convenience methods.
 * 
 * @author th
 *
 */
public class RssOwlInternals {
	// Feld 0: Titel?
	// Feld 1: URL
	// Feld 2: Description
	// Feld 5: URL
	// Feld 17: Source?
	// Feld 21: Timestamp

	private static final String OWLIDX_OBJ_REFERENCE = "-2";

	private static final String OWLIDX_FIELD_DESCRIPTION = "2";

	private static final String NEWS_ID_FIELD = "fId";
	private static final String NEWS_TITLE = "fTitle";
	private static final String DESCRIPTION_NEWS_REFERENCE = "fNewsId";

	/**
	 * "One news please" /zoidberg
	 * 
	 * @param objectContainer
	 * @param newsId
	 * @return
	 */
	public static News retrieveNews(ObjectContainer objectContainer, Long newsId) {
		Query query = objectContainer.query();
		query.constrain(News.class);
		query.descend(NEWS_ID_FIELD).constrain(newsId);

		News news = (News) query.execute().get(0);
		objectContainer.activate(news, Integer.MAX_VALUE);

		return news;
	}

	/**
	 * "One description please" /zoidberg
	 * 
	 * @param objectContainer
	 * @param newsId
	 * @return
	 */
	public static Description retrieveDescription(ObjectContainer objectContainer, Long newsId) {
		Query query = objectContainer.query();
		query.constrain(Description.class);
		query.descend(DESCRIPTION_NEWS_REFERENCE).constrain(newsId);

		Description description = (Description) query.execute().get(0);
		objectContainer.activate(description, Integer.MAX_VALUE);

		return description;
	}

	public static News retrieveNews(ObjectContainer objectContainer, Document document) {
		return retrieveNews(objectContainer, new Long(document.get(OWLIDX_OBJ_REFERENCE)));
	}

	public static QueryParser getQueryParser() {
		return new QueryParser(OWLIDX_FIELD_DESCRIPTION, new StandardAnalyzer());
	}

	public static Description retrieveDescription(ObjectContainer objectContainer, Document document) {
		return retrieveDescription(objectContainer, new Long(document.get(OWLIDX_OBJ_REFERENCE)));
	}

	public static String getNewsTitle(News news) {
		try {
			Field field = news.getClass().getDeclaredField(NEWS_TITLE);
			field.setAccessible(true);
			return (String) field.get(news);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

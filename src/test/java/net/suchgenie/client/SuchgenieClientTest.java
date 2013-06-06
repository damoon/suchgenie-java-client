package net.suchgenie.client;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class SuchgenieClientTest
{
	private static SuchgenieClient client = SuchgenieClient
			.builder()
			.withDatabase("test")
			.withAuthentication("test", "test")
			.withUserIdFactory(new BotUserIdFactory())
			.build();
	private static EventLogger eventLogger = client.getEventLogger();

	@Test(expected=SuchgenieException.class)
	public void unauthorized() throws SuchgenieException
	{
		SuchgenieClient unauthorizedClient = SuchgenieClient
				.builder()
				.withDatabase("test")
				.build();
		
		unauthorizedClient
				.createRequest("bot")
				.setQuery("a")
				.getAutocompletions(5);
	}

	@Test
	public void autocompletions() throws SuchgenieException
	{
		Autocompletions autocompletions = client
				.createRequest()
				.setQuery("a")
				.getAutocompletions(5);
		assertEquals("Auto", autocompletions.get(0).value);
		assertEquals(5, autocompletions.size());
	}

	@Test
	public void documentIdentifiers() throws SuchgenieException
	{
		DocumentIdentifiers documentIdentifiers = client
				.createRequest()
				.setQuery("sonne")
				.getDocumentIdentifiers();

		assertEquals(2, documentIdentifiers.documentCount);
		assertEquals(1, documentIdentifiers.pageCount);
		assertEquals(1, documentIdentifiers.pageNumber);
		assertEquals(10, documentIdentifiers.resultsPerPage);
		assertEquals(2, documentIdentifiers.documentIdentifiers.size());
		assertTrue(documentIdentifiers.documentIdentifiers.contains("1"));
		assertTrue(documentIdentifiers.documentIdentifiers.contains("13"));
	}

	@Test
	public void documents() throws SuchgenieException
	{
		List<String> attributes = new LinkedList<>();
		attributes.add("id");
		attributes.add("word");
		attributes.add("results");
		
		Documents documents = client
				.createRequest()
				.setQuery("sonne")
				.getDocuments(attributes);

		assertEquals(2, documents.documentCount);
		assertEquals(1, documents.pageCount);
		assertEquals(1, documents.pageNumber);
		assertEquals(10, documents.resultsPerPage);
		assertEquals(2, documents.documents.size());
		assertEquals("1", documents.documents.get(0).get("id"));
		assertEquals("13", documents.documents.get(1).get("id"));
	}

	@Test
	public void navigation() throws SuchgenieException
	{
		List<String> attributes = new LinkedList<>();
		attributes.add("id");
		attributes.add("word");
		
		Navigation navigation = client
				.createRequest()
				.setQuery("sonne")
				.getNavigation(attributes);

		assertEquals(2, navigation.size());
		assertEquals(2, navigation.get("id").size());
		assertEquals(Integer.valueOf(1), navigation.get("id").get("1"));
		assertEquals(Integer.valueOf(1), navigation.get("id").get("13"));
		assertEquals(2, navigation.get("word").size());
		assertEquals(Integer.valueOf(1), navigation.get("word").get("Sonnencreme"));
		assertEquals(Integer.valueOf(1), navigation.get("word").get("Sonnensystem"));
	}

	@Test
	public void documentIdentifiersAndNavigation() throws SuchgenieException
	{
		List<String> attributes = new LinkedList<>();
		attributes.add("id");
		attributes.add("word");
		
		DocumentIdentifiersAndNavigation documentIdentifiersAndNavigation = client
				.createRequest()
				.setQuery("sonne")
				.getDocumentIdentifiersAndNavigation(attributes);

		DocumentIdentifiers documentIdentifiers = documentIdentifiersAndNavigation.documentIdentifiers;
		Navigation navigation = documentIdentifiersAndNavigation.navigation;
		
		assertEquals(2, documentIdentifiers.documentCount);
		assertEquals(1, documentIdentifiers.pageCount);
		assertEquals(1, documentIdentifiers.pageNumber);
		assertEquals(10, documentIdentifiers.resultsPerPage);
		assertEquals(2, documentIdentifiers.documentIdentifiers.size());
		assertTrue(documentIdentifiers.documentIdentifiers.contains("1"));
		assertTrue(documentIdentifiers.documentIdentifiers.contains("13"));

		assertEquals(2, navigation.size());
		assertEquals(2, navigation.get("id").size());
		assertEquals(Integer.valueOf(1), navigation.get("id").get("1"));
		assertEquals(Integer.valueOf(1), navigation.get("id").get("13"));
		assertEquals(2, navigation.get("word").size());
		assertEquals(Integer.valueOf(1), navigation.get("word").get("Sonnencreme"));
		assertEquals(Integer.valueOf(1), navigation.get("word").get("Sonnensystem"));
	}

	@Test
	public void documentsAndNavigation() throws SuchgenieException
	{
		List<String> documentAttributes = new LinkedList<>();
		documentAttributes.add("id");
		documentAttributes.add("word");
		documentAttributes.add("results");

		List<String> navigationAttributes = new LinkedList<>();
		navigationAttributes.add("id");
		navigationAttributes.add("word");
		
		DocumentsAndNavigation documentsAndNavigation = client
				.createRequest()
				.setQuery("sonne")
				.getDocumentsAndNavigation(documentAttributes, navigationAttributes);

		Documents documents = documentsAndNavigation.documents;
		Navigation navigation = documentsAndNavigation.navigation;
		
		assertEquals(2, documents.documentCount);
		assertEquals(1, documents.pageCount);
		assertEquals(1, documents.pageNumber);
		assertEquals(10, documents.resultsPerPage);
		assertEquals(2, documents.documents.size());
		assertEquals("1", documents.documents.get(0).get("id"));
		assertEquals("13", documents.documents.get(1).get("id"));
		
		assertEquals(2, navigation.size());
		assertEquals(2, navigation.get("id").size());
		assertEquals(Integer.valueOf(1), navigation.get("id").get("1"));
		assertEquals(Integer.valueOf(1), navigation.get("id").get("13"));
		assertEquals(2, navigation.get("word").size());
		assertEquals(Integer.valueOf(1), navigation.get("word").get("Sonnencreme"));
		assertEquals(Integer.valueOf(1), navigation.get("word").get("Sonnensystem"));
	}

	@Test
	public void logDocumentView() throws SuchgenieException
	{
		assertTrue(eventLogger.logDocumentView("documentIdentifier").successful);
	}

	@Test
	public void logOrder() throws SuchgenieException
	{
		List<String> documentIdentifiersList = new LinkedList<>();
		documentIdentifiersList.add("documentIdentifier1");
		documentIdentifiersList.add("documentIdentifier2");
		assertTrue(eventLogger.logOrder(documentIdentifiersList).successful);
	}

	@Test
	public void logPreparedOrder() throws SuchgenieException
	{
		assertTrue(eventLogger.logPreparedOrder("documentIdentifier").successful);
	}

	@Test
	public void logSearch() throws SuchgenieException
	{
		assertTrue(eventLogger.logSearch("query").successful);
	}

	@Test
	public void logSearchExtended() throws SuchgenieException
	{
		assertTrue(eventLogger.logSearchExtended("query").successful);
	}
}

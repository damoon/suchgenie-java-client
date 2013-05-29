package net.suchgenie.client;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.api.client.util.Joiner;
import com.google.api.client.util.Preconditions;

public class Request extends ConnectionEvent
{
	private Map<String, String> defaultParameters = new HashMap<>();
	private Map<String, String> documentsParameters = new HashMap<>();

	Request(ConnectionHandler requester, String userId)
	{
		super(requester, userId);
	}

	public Autocompletions getAutocompletions(int numberOfAutocompletions) throws SuchgenieException
	{
		Map<String, String> params = getUserIdAndTimestamp();
		params.put("query", Preconditions.checkNotNull(Preconditions.checkNotNull(defaultParameters.get("query"))));
		params.put("numberOfAutocompletions", String.valueOf(numberOfAutocompletions));
		try
		{
			return requester.executeParallelGetRequest("/api/autocompletions.json", params).parseAs(Autocompletions.class);
		}
		catch (IOException e)
		{
			throw new SuchgenieException(e);
		}
	}

	public Navigation getNavigation(Collection<String> attributes) throws SuchgenieException
	{
		// query, comparators, filters, attributes
		Map<String, String> params = getUserIdAndTimestamp();
		params.putAll(defaultParameters);
		params.put("attributes", Joiner.on(',').join(attributes));
		try
		{
			return requester.executeParallelGetRequest("/api/navigation.json", params).parseAs(Navigation.class);
		}
		catch (IOException e)
		{
			throw new SuchgenieException(e);
		}
	}

	public Documents getDocuments(Collection<String> attributes) throws SuchgenieException
	{
		// query, documentsPerPage, pageNumber, comparators, filters, sortings,
		// attributes
		Map<String, String> params = getUserIdAndTimestamp();
		params.putAll(defaultParameters);
		params.putAll(documentsParameters);
		params.put("attributes", commaJoiner.join(attributes));
		try
		{
			return requester.executeParallelGetRequest("/api/documents.json", params).parseAs(Documents.class);
		}
		catch (IOException e)
		{
			throw new SuchgenieException(e);
		}
	}

	public DocumentIdentifiers getDocumentIdentifiers() throws SuchgenieException
	{
		// query, documentsPerPage, pageNumber, comparators, filters, sortings
		Map<String, String> params = getUserIdAndTimestamp();
		params.putAll(defaultParameters);
		params.putAll(documentsParameters);
		try
		{
			return requester.executeParallelGetRequest("/api/documentIdentifiers.json", params).parseAs(DocumentIdentifiers.class);
		}
		catch (IOException e)
		{
			throw new SuchgenieException(e);
		}
	}

	public Request setQuery(String query)
	{
		defaultParameters.put("query", query);
		return this;
	}

	public Request setDocumentsPerPage(int documentsPerPage)
	{
		documentsParameters.put("documentsPerPage", String.valueOf(documentsPerPage));
		return this;
	}

	public Request setPageNumber(int pageNumber)
	{
		documentsParameters.put("pageNumber", String.valueOf(pageNumber));
		return this;
	}

	public Request setComparator(String attribute, String comparator)
	{
		defaultParameters.put(attribute + "Comparator", comparator);
		return this;
	}

	public Request setSorting(String attribute, Direction direction)
	{
		defaultParameters.put("sort" + attribute, direction.toString().toLowerCase());
		return this;
	}

	public Request setFilter(String attribute, String comparisonType, String value)
	{
		if (value == null)
		{
			removeFilter(attribute, comparisonType);
		}
		else
		{
			defaultParameters.put("filter" + attribute + comparisonType, value);
		}
		return this;
	}

	public Request removeFilter(String attribute, String comparisonType)
	{
		defaultParameters.remove("filter" + attribute + comparisonType);
		return this;
	}

	public Request setEqualFilter(String attribute, String value)
	{
		setFilter(attribute, "eq", value);
		return this;
	}

	public Request setNotEqualFilter(String attribute, String value)
	{
		setFilter(attribute, "ne", value);
		return this;
	}

	public Request setGreaterEqualFilter(String attribute, String value)
	{
		setFilter(attribute, "ge", value);
		return this;
	}

	public Request setGreaterThenFilter(String attribute, String value)
	{
		setFilter(attribute, "gt", value);
		return this;
	}

	public Request setLessEqualFilter(String attribute, String value)
	{
		setFilter(attribute, "le", value);
		return this;
	}

	public Request setLessThenFilter(String attribute, String value)
	{
		setFilter(attribute, "lt", value);
		return this;
	}
}

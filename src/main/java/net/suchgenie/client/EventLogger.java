package net.suchgenie.client;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class EventLogger extends ConnectionEvent
{
	EventLogger(ConnectionHandler requester, String userId)
	{
		super(requester, userId);
	}

	public LoggingResult logSearch(String query) throws SuchgenieException
	{
		Map<String, String> params = getUserIdAndTimestamp();
		params.put("event", "search");
		params.put("query", query);
		try
		{
			return requester.executeParallelPostRequest("/api/log.json", params).parseAs(LoggingResult.class);
		}
		catch (IOException e)
		{
			throw new SuchgenieException(e);
		}
	}

	public LoggingResult logSearchExtended(String query) throws SuchgenieException
	{
		Map<String, String> params = getUserIdAndTimestamp();
		params.put("event", "searchExtended");
		params.put("query", query);
		try
		{
			return requester.executeParallelPostRequest("/api/log.json", params).parseAs(LoggingResult.class);
		}
		catch (IOException e)
		{
			throw new SuchgenieException(e);
		}
	}

	public LoggingResult logDocumentView(String documentIdentifier) throws SuchgenieException
	{
		Map<String, String> params = getUserIdAndTimestamp();
		params.put("event", "documentView");
		params.put("documentIdentifier", documentIdentifier);
		try
		{
			return requester.executeParallelPostRequest("/api/log.json", params).parseAs(LoggingResult.class);
		}
		catch (IOException e)
		{
			throw new SuchgenieException(e);
		}
	}

	public LoggingResult logPreparedOrder(String documentIdentifier) throws SuchgenieException
	{
		Map<String, String> params = getUserIdAndTimestamp();
		params.put("event", "preparedOrder");
		params.put("documentIdentifier", documentIdentifier);
		try
		{
			return requester.executeParallelPostRequest("/api/log.json", params).parseAs(LoggingResult.class);
		}
		catch (IOException e)
		{
			throw new SuchgenieException(e);
		}
	}

	public LoggingResult logOrder(Collection<String> documentIdentifiersList) throws SuchgenieException
	{
		Map<String, String> params = getUserIdAndTimestamp();
		params.put("event", "order");
		params.put("documentIdentifiers", commaJoiner.join(documentIdentifiersList));
		try
		{
			return requester.executeParallelPostRequest("/api/log.json", params).parseAs(LoggingResult.class);
		}
		catch (IOException e)
		{
			throw new SuchgenieException(e);
		}
	}

}

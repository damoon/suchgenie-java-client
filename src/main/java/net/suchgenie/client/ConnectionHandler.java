package net.suchgenie.client;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

class ConnectionHandler
{
	private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

	private static final Executor EXECUTOR = new ThreadPoolExecutor(2, 32, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	private final HttpRequestFactory httpRequestFactory;

	private final String database;
	private final ServerSelectionPolicy serverSelectionPolicy;

	ConnectionHandler(String database, ServerSelectionPolicy serverSelectionPolicy, HttpRequestFactory httpRequestFactory)
	{
		this.database = database;
		this.serverSelectionPolicy = serverSelectionPolicy;
		this.httpRequestFactory = httpRequestFactory;
	}

	HttpResponse executeParallelGetRequest(String path, Map<String, String> params) throws IOException, SuchgenieException
	{
		Collection<HttpRequest> requests = new LinkedList<>();
		for (String domain : serverSelectionPolicy.getDomains(database))
		{
			GenericUrl url = new GenericUrl("http://" + domain + path);
			for (Entry<String, String> entry : params.entrySet())
			{
				url.set(entry.getKey(), entry.getValue());
			}
			requests.add(httpRequestFactory.buildGetRequest(url));
		}
		return getFirstResponse(requests);
	}

	HttpResponse executeParallelPostRequest(String path, Map<String, String> params) throws IOException, SuchgenieException
	{
		Collection<HttpRequest> requests = new LinkedList<>();
		for (String domain : serverSelectionPolicy.getDomains(database))
		{
			GenericUrl url = new GenericUrl("http://" + domain + path);
			for (Entry<String, String> entry : params.entrySet())
			{
				url.set(entry.getKey(), entry.getValue());
			}
			requests.add(httpRequestFactory.buildPostRequest(url, null));
		}
		return getFirstResponse(requests);
	}

	private HttpResponse getFirstResponse(Collection<HttpRequest> requests) throws SuchgenieException
	{
		Collection<Future<HttpResponse>> futureHttpResponses = new LinkedList<>();
		for (HttpRequest request : requests)
		{
			futureHttpResponses.add(request.executeAsync(EXECUTOR));
		}

		while (!futureHttpResponses.isEmpty())
		{
			for (Future<HttpResponse> futureHttpResponse : futureHttpResponses)
			{
				try
				{
					HttpResponse response = futureHttpResponse.get(100, TimeUnit.MILLISECONDS);
					if (response.isSuccessStatusCode())
					{
						return response;
					}
					logger.warn("HttpRequest failed with status code {}", response.isSuccessStatusCode());
					futureHttpResponses.remove(futureHttpResponse);
				}
				catch (InterruptedException e)
				{
					logger.warn("HttpRequest got interrupted while execution", e);
					futureHttpResponses.remove(futureHttpResponse);
					Thread.currentThread().interrupt();
				}
				catch (ExecutionException e)
				{
					futureHttpResponses.remove(futureHttpResponse);
					if (futureHttpResponses.isEmpty())
					{
						throw new SuchgenieException(e.getCause());
					}
					if (!e.getCause().getMessage().startsWith("401 Unauthorized"))
					{
						logger.warn("logging failure of not last parallel HttpRequest", e);
					}
				}
				catch (TimeoutException e)
				{
					logger.trace("HttpRequest has no result, yet. Checking the next.", e);
				}
			}
		}

		throw new SuchgenieException("The Request could not be completed. Sorry.");
	}
}

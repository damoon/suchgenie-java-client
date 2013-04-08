package net.suchgenie.client;

import java.io.IOException;

import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;

public class Builder
{
	private String database;
	private int connectTimeout = 5000;
	private int readTimeout = 10000;

	private ServerSelectionPolicy serverSelectionPolicy = new DefaultServerSelectionPolicy();
	private UserIdFactory userIdFactory;
	private HttpTransport httpTransport = new NetHttpTransport();
	private JsonFactory jsonFactory = new JacksonFactory();
	
	private String username;
	private String password;

	protected Builder()
	{
		// just to force the creation by Client.build().(...).initiate();
	}

	@SuppressWarnings("hiding")
	public Builder withDatabase(String database)
	{
		this.database = database;
		return this;
	}

	@SuppressWarnings("hiding")
	public Builder withConnectTimeout(int connectTimeout)
	{
		this.connectTimeout = connectTimeout;
		return this;
	}

	@SuppressWarnings("hiding")
	public Builder withReadTimeout(int readTimeout)
	{
		this.readTimeout = readTimeout;
		return this;
	}

	@SuppressWarnings("hiding")
	public Builder withAuthentication(String username, String password)
	{
		this.username = Preconditions.checkNotNull(username);
		this.password = Preconditions.checkNotNull(password);
		return this;
	}

	@SuppressWarnings("hiding")
	public Builder withServerSelectionPolicy(ServerSelectionPolicy serverSelectionPolicy)
	{
		this.serverSelectionPolicy = serverSelectionPolicy;
		return this;
	}

	@SuppressWarnings("hiding")
	public Builder withUserIdFactory(UserIdFactory userIdFactory)
	{
		this.userIdFactory = userIdFactory;
		return this;
	}

	@SuppressWarnings("hiding")
	public Builder withHttpTransport(HttpTransport httpTransport)
	{
		this.httpTransport = httpTransport;
		return this;
	}

	@SuppressWarnings("hiding")
	public Builder withJsonFactory(JsonFactory jsonFactory)
	{
		this.jsonFactory = jsonFactory;
		return this;
	}

	public SuchgenieClient build()
	{
		HttpRequestInitializer initializer;
		if (username != null)
		{
			initializer = new AuthenticatedInitializer(jsonFactory, connectTimeout, readTimeout, username, password);
		}
		else
		{
			initializer = new BasicInitializer(jsonFactory, connectTimeout, readTimeout);
		}

		HttpRequestFactory requestFactory = httpTransport.createRequestFactory(initializer);

		return new SuchgenieClient(userIdFactory, new ConnectionHandler(Preconditions.checkNotNull(database), serverSelectionPolicy, requestFactory));
	}

	private class BasicInitializer implements HttpRequestInitializer
	{
		private final JsonObjectParser jsonObjectParser;
		private final int connectTimeout;
		private final int readTimeout;

		BasicInitializer(JsonFactory jsonFactory, int connectTimeout, int readTimeout)
		{
			this.jsonObjectParser = new JsonObjectParser(jsonFactory);
			this.connectTimeout = connectTimeout;
			this.readTimeout = readTimeout;
		}

		public void initialize(HttpRequest request) throws IOException
		{
			request.setParser(jsonObjectParser);
			request.setConnectTimeout(connectTimeout);
			request.setReadTimeout(readTimeout);
			request.getHeaders().setUserAgent(SuchgenieClient.USER_AGENT);
		}
	}

	private class AuthenticatedInitializer extends BasicInitializer implements HttpExecuteInterceptor
	{
		private final String username;

		private final String password;

		AuthenticatedInitializer(JsonFactory jsonFactory, int connectTimeout, int readTimeout, String username, String password)
		{
			super(jsonFactory, connectTimeout, readTimeout);
			this.username = username;
			this.password = password;
		}

		public void initialize(HttpRequest request) throws IOException
		{
			super.initialize(request);
			request.setInterceptor(this);
		}

		public void intercept(HttpRequest request)
		{
			request.getHeaders().setBasicAuthentication(username, password);
		}
	}
}

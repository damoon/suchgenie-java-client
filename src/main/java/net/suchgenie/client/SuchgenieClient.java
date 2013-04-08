package net.suchgenie.client;

import com.google.api.client.util.Joiner;

public class SuchgenieClient
{
	static final String USER_AGENT = "suchgenie-java-client/1.0.0";

	static final Joiner commaJoiner = Joiner.on(',');

	private final UserIdFactory userIdFactory;
	private final ConnectionHandler requester;

	SuchgenieClient(UserIdFactory userIdFactory, ConnectionHandler requester)
	{
		this.userIdFactory = userIdFactory;
		this.requester = requester;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public Request createRequest(String userId)
	{
		return new Request(requester, userId);
	}

	public Request createRequest()
	{
		return new Request(requester, userIdFactory.getUserId());
	}

	public EventLogger getEventLogger(String userId)
	{
		return new EventLogger(requester, userId);
	}

	public EventLogger getEventLogger()
	{
		return new EventLogger(requester, userIdFactory.getUserId());
	}
}

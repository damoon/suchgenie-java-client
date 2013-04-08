package net.suchgenie.client;

import java.util.HashMap;
import java.util.Map;

import com.google.api.client.util.Joiner;

abstract class ConnectionEvent
{
	static final Joiner commaJoiner = Joiner.on(',');
	final ConnectionHandler requester;
	private final String userId;
	
	ConnectionEvent(ConnectionHandler requester, String userId)
	{
		this.requester = requester;
		this.userId = userId;
	}
	
	protected Map<String, String> getUserIdAndTimestamp()
	{
		// from https://github.com/s7/scale7-pelops/issues/52
		long currentTimeMillis = System.currentTimeMillis();
		long nanoTime = System.nanoTime();
		long currentTimeMicros = currentTimeMillis * 1000 + nanoTime / 1000 - (nanoTime > 1000000 ? (nanoTime / 1000000) * 1000 : 0);
		
		Map<String, String> params = new HashMap<>();
		params.put("userId", userId);
		params.put("ts", Long.toString(currentTimeMicros));
		return params;
	}
}

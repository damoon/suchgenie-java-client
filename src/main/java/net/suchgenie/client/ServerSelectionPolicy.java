package net.suchgenie.client;

import java.util.Collection;

public interface ServerSelectionPolicy
{
	public Collection<String> getDomains(String database);
}

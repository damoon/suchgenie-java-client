package net.suchgenie.client;

import com.google.api.client.util.Key;

public class DocumentIdentifiersAndNavigation
{
	@Key("page")
	public DocumentIdentifiers documentIdentifiers;

	@Key
	public Navigation navigation;
}

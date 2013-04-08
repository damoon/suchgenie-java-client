package net.suchgenie.client;

import java.util.List;

import com.google.api.client.util.Key;

public class DocumentIdentifiers
{
	@Key
	public int documentCount;

	@Key
	public int pageCount;

	@Key
	public int pageNumber;

	@Key
	public int resultsPerPage;

	@Key
	public List<String> documentIdentifiers;
}

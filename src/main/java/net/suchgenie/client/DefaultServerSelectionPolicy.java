package net.suchgenie.client;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

public class DefaultServerSelectionPolicy implements ServerSelectionPolicy
{
	private final static String VERISIGN_DOMAIN = "suchgenie.com";
	private final static String DENIC_DOMAIN = "suchgenie-backup.de";
	private final static Random random = new Random();

	@Override
	public Collection<String> getDomains(String database)
	{
		Collection<String> domains = new LinkedList<>();
		if (random.nextBoolean())
		{
			domains.add(database + "1." + VERISIGN_DOMAIN);
			domains.add(database + "2." + DENIC_DOMAIN);
		}
		else
		{
			domains.add(database + "2." + VERISIGN_DOMAIN);
			domains.add(database + "1." + DENIC_DOMAIN);
		}
		return domains;
	}
}

package net.suchgenie.client;

public class BotUserIdFactory implements UserIdFactory
{

	@Override
	public String getUserId()
	{
		return "bot";
	}

}

package net.suchgenie.client;

public class SuchgenieException extends Exception
{
	public SuchgenieException(Throwable throwable)
	{
		super(throwable);
	}

	public SuchgenieException(String string)
	{
		super(string);
	}
}

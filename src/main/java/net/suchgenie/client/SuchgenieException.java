package net.suchgenie.client;

public class SuchgenieException extends Exception
{
	private static final long serialVersionUID = 1L;

	public SuchgenieException(Throwable throwable)
	{
		super(throwable);
	}

	public SuchgenieException(String string)
	{
		super(string);
	}
}

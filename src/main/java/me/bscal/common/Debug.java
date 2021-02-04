package me.bscal.common;

import me.bscal.logcraft.LogCraft;

public class Debug
{

	private static LogCraft m_logger;

	public static void Init(LogCraft logger)
	{
		m_logger = logger;
	}

	public static boolean HasLogger()
	{
		return m_logger != null;
	}


	public static LogCraft GetLogger()
	{
		return m_logger;
	}

}

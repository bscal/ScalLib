package me.bscal.SQLAPI;

import org.bukkit.plugin.Plugin;

public class Database
{

	public final Plugin pl;
	public final String host;
	public final String port;
	public final String user;
	public final String pass;
	public final String db;
	public final String table;

	public Database(Plugin pl, String host, String port, String user, String pass, String db)
	{
		this.pl = pl;
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.db = db;
		this.table = pl.getName();
	}

	public Database(Plugin pl, String host, String port, String user, String pass, String db,
			String table)
	{
		this.pl = pl;
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.db = db;
		this.table = table;
	}

}

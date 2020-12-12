package me.bscal.SQLAPI;

import me.bscal.logcraft.LogCraft;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.util.function.Consumer;

public class BukkitSQLAPI extends SQLAPI
{
	public BukkitSQLAPI(Database connection, LogCraft logger, boolean mysqlEnabled)
	{
		super(connection, logger, mysqlEnabled);
	}

	/*-
	 * *************************************
	 * * Saving and Loading Status Players *
	 * *************************************
	 */

	public static DBKeyValue ToKey(final Player p)
	{
		return new DBKeyValue("UUID", p.getUniqueId().toString());
	}

	/*-
	 * *************************************
	 * * SQL Async functions with Bukkit   *
	 * *************************************
	 */

	public void AsyncInsert(final String table, final DBKeyValue... columns)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				Insert(table, columns);
			}
		}.runTaskAsynchronously(m_database.pl);
	}

	public void AsyncSelect(final String table, final DBSelect columns, Consumer<ResultSet> cb)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				cb.accept(Select(table, columns));
			}
		}.runTaskAsynchronously(m_database.pl);
	}

	public void AsyncUpdate(final String table, final DBUpdate columns)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				Update(table, columns);
			}
		}.runTaskAsynchronously(m_database.pl);
	}

	public void AsyncDelete(final String table, final DBUpdate columns)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				Delete(table, columns);
			}
		}.runTaskAsynchronously(m_database.pl);
	}

	public void GetVar(final String table, final String column, final Player p,
			Consumer<ResultSet> cb)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				cb.accept(SelectVar(table, column, p.getUniqueId().toString()));
			}
		}.runTaskAsynchronously(m_database.pl);
	}

	public void SetVar(final String table, final String column, final Object value, final Player p)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				UpdateVar(table, column, value, p.getUniqueId().toString());
			}
		}.runTaskAsynchronously(m_database.pl);
	}
}

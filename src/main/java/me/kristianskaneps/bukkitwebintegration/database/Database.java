package me.kristianskaneps.bukkitwebintegration.database;

import me.kristianskaneps.bukkitwebintegration.config.DatabaseConnectionConfig;
import me.kristianskaneps.bukkitwebintegration.config.DatabaseTableConfig;
import me.kristianskaneps.bukkitwebintegration.database.table.PlayersTable;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class Database
{
	private static final AtomicReference<Database> INSTANCE = new AtomicReference<>();
	public static void setInstance(Database instance) { INSTANCE.set(instance); }

	private PlayersTable playersTable;

	private Connection conn;

	protected final DatabaseConnectionConfig connectionConfig;
	protected final DatabaseTableConfig tableConfig;

	public Database(DatabaseConnectionConfig connectionConfig, DatabaseTableConfig tableConfig)
	{
		this.connectionConfig = connectionConfig;
		this.tableConfig = tableConfig;
	}

	public static PlayersTable playersTable()
	{
		synchronized (INSTANCE)
		{
			return INSTANCE.get().playersTable;
		}
	}

	public static DSLContext query()
	{
		synchronized (INSTANCE)
		{
			return INSTANCE.get().dsl();
		}
	}

	public DSLContext dsl()
	{
		synchronized (INSTANCE)
		{
			final Database db = INSTANCE.get();
			try {
				if(!db.isConnected()) db.connect();
				return DSL.using(conn, SQLDialect.MYSQL);
			} catch(DatabaseException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public boolean isConnected()
	{
		synchronized (INSTANCE)
		{
			try {
				return conn != null && !conn.isClosed();
			} catch(SQLException e) {
				return false;
			}
		}
	}

	public void connect() throws DatabaseException
	{
		synchronized (INSTANCE)
		{
			try {
				if(conn != null && !conn.isClosed()) return;
				if(conn != null) conn.close();
				conn = DriverManager.getConnection(
						connectionConfig.url,
						connectionConfig.username,
						connectionConfig.password
				);
				new Structure(this).create();
				initializeTableReferences();
			} catch(SQLException e) {
				throw new DatabaseException(e);
			}
		}
	}

	public void disconnect()
	{
		synchronized (INSTANCE)
		{
			try {
				if(conn == null || conn.isClosed()) return;
				conn.close();
				conn = null;
			} catch(SQLException ignored) {}
		}
	}

	private void initializeTableReferences()
	{
		playersTable = new PlayersTable(tableConfig.players);
	}
}

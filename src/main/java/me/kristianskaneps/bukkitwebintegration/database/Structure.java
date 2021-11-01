package me.kristianskaneps.bukkitwebintegration.database;

import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.SQLException;

public class Structure
{
	private final Database database;

	protected Structure(Database database)
	{
		this.database = database;
	}

	protected void create() throws SQLException
	{
		database.dsl()
				.createTableIfNotExists(database.tableConfig.players)
				.column("id", SQLDataType.BIGINTUNSIGNED.identity(true))
				.column("uuid", SQLDataType.UUID.nullable(false))
				.column("name", SQLDataType.VARCHAR(16).collation(DSL.collation("ascii_general_ci")).nullable(false))
				.column("online", SQLDataType.BOOLEAN.nullable(false).defaultValue(true))
				.column("authenticated", SQLDataType.BOOLEAN.nullable(false).defaultValue(false))
				.column("last_login", SQLDataType.LOCALDATETIME.nullable(true))
				.column("last_logout", SQLDataType.LOCALDATETIME.nullable(true))
				.column("login_world", SQLDataType.VARCHAR(63).nullable(true))
				.column("login_x", SQLDataType.DOUBLE.nullable(true))
				.column("login_y", SQLDataType.DOUBLE.nullable(true))
				.column("login_z", SQLDataType.DOUBLE.nullable(true))
				.column("logout_world", SQLDataType.VARCHAR(63).nullable(true))
				.column("logout_x", SQLDataType.DOUBLE.nullable(true))
				.column("logout_y", SQLDataType.DOUBLE.nullable(true))
				.column("logout_z", SQLDataType.DOUBLE.nullable(true))
				.column("ip", SQLDataType.VARCHAR(15).nullable(true))
				.column("created_at", SQLDataType.LOCALDATETIME.nullable(false))
				.unique("uuid")
				.primaryKey("id")
				.execute();
		database.dsl()
				.createTableIfNotExists(database.tableConfig.playerCredentials)
				.column("id", SQLDataType.BIGINTUNSIGNED.identity(true))
				.column("uuid", SQLDataType.UUID.nullable(false))
				.column("password", SQLDataType.VARCHAR(255).nullable(false))
				.column("updated_at", SQLDataType.LOCALDATETIME.nullable(false))
				.column("created_at", SQLDataType.LOCALDATETIME.nullable(false))
				.unique("uuid")
				.primaryKey("id")
				.execute();
	}
}

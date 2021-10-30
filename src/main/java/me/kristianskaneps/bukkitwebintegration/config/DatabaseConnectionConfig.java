package me.kristianskaneps.bukkitwebintegration.config;

import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseConnectionConfig
{
	public final String url, username, password;

	protected DatabaseConnectionConfig(FileConfiguration config)
	{
		url = config.getString("database.url");
		username = config.getString("database.username");
		password = config.getString("database.password");
	}
}

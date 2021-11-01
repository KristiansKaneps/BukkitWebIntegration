package me.kristianskaneps.bukkitwebintegration.config;

import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseTableConfig
{
	public final String players, playerCredentials;

	protected DatabaseTableConfig(FileConfiguration config)
	{
		this.players = config.getString("database.table.players");
		this.playerCredentials = config.getString("database.table.player_credentials");
	}
}

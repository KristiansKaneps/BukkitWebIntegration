package me.kristianskaneps.bukkitwebintegration.config;

import me.kristianskaneps.bukkitwebintegration.WebIntegration;

import java.util.concurrent.atomic.AtomicReference;

public final class ConfigManager
{
	public static final AtomicReference<DatabaseConnectionConfig> databaseConnection = new AtomicReference<>();
	public static final AtomicReference<DatabaseTableConfig> databaseTables = new AtomicReference<>();

	private final WebIntegration instance;

	public ConfigManager(WebIntegration instance)
	{
		this.instance = instance;
	}

	public void loadConfigs()
	{
		instance.getConfig().options().copyDefaults(true);
		instance.saveConfig();

		databaseConnection.set(new DatabaseConnectionConfig(instance.getConfig()));
		databaseTables.set(new DatabaseTableConfig(instance.getConfig()));
	}
}

package me.kristianskaneps.bukkitwebintegration;

import me.kristianskaneps.bukkitwebintegration.config.ConfigManager;
import me.kristianskaneps.bukkitwebintegration.database.Database;
import me.kristianskaneps.bukkitwebintegration.database.DatabaseException;
import me.kristianskaneps.bukkitwebintegration.events.PlayerEvents;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class WebIntegration extends JavaPlugin
{
	public static final String NAME = "WebIntegration";
	public static final String PREFIX = "[" + NAME + "] ";

	public static Logger log;

	private Database database;

	@Override
	public void onEnable()
	{
		final Server server = this.getServer();
		final PluginManager pm = server.getPluginManager();
		log = server.getLogger();
		final ConfigManager configManager = new ConfigManager(this);

		configManager.loadConfigs();

		try {
			this.database = initializeDatabaseConnection();
			pm.registerEvents(new PlayerEvents(this), this);
		} catch(DatabaseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable()
	{
		if(database != null) database.disconnect();
	}

	private Database initializeDatabaseConnection() throws DatabaseException
	{
		final Database database = new Database(
				ConfigManager.databaseConnection.get(),
				ConfigManager.databaseTables.get()
		);
		Database.setInstance(database);
		database.connect();
		return database;
	}
}

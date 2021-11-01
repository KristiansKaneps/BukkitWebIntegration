package me.kristianskaneps.bukkitwebintegration;

import me.kristianskaneps.bukkitwebintegration.commands.AuthCommand;
import me.kristianskaneps.bukkitwebintegration.config.ConfigManager;
import me.kristianskaneps.bukkitwebintegration.database.Database;
import me.kristianskaneps.bukkitwebintegration.database.DatabaseException;
import me.kristianskaneps.bukkitwebintegration.events.PlayerEvents;
import me.kristianskaneps.bukkitwebintegration.events.UnauthenticatedPlayerEvents;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WebIntegration extends JavaPlugin
{
	public static final String NAME = "WebIntegration";
	public static final String PREFIX = "[" + NAME + "] ";

	public static Logger log;

	private Database database;

	private final List<NotificationListener> notificationListeners = new ArrayList<>();

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

			final PlayerEvents playerEvents = new PlayerEvents(this);
			final UnauthenticatedPlayerEvents unauthenticatedPlayerEvents = new UnauthenticatedPlayerEvents(this);
			pm.registerEvents(playerEvents, this);
			pm.registerEvents(unauthenticatedPlayerEvents, this);

			registerNotificationListener(unauthenticatedPlayerEvents);

			new AuthCommand(this).setAsExecutorFor("register", "login", "logout");
		} catch(DatabaseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable()
	{
		if(database != null) database.disconnect();
	}

	public void registerNotificationListener(NotificationListener listener)
	{
		synchronized (notificationListeners)
		{
			notificationListeners.add(listener);
		}
	}

	public void pushNotification(Notification notification)
	{
		synchronized (notificationListeners)
		{
			for(final NotificationListener listener : notificationListeners)
				for(final Notification.Type type : listener.getSupportedNotifications())
					if(type == notification.type)
					{
						listener.onNotification(notification);
						break;
					}
		}
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

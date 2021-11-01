package me.kristianskaneps.bukkitwebintegration.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public abstract class Executor<Plugin extends JavaPlugin> implements CommandExecutor
{
	protected abstract Plugin getPluginInstance();

	public final void setAsExecutorFor(String... commands)
	{
		final Plugin instance = getPluginInstance();
		for(final String label : commands)
		{
			final PluginCommand command = instance.getCommand(label);
			if(command == null)
			{
				instance.getServer().getLogger().log(Level.WARNING, "Command '" + label + "' won't be available.");
				continue;
			}
			command.setExecutor(this);
		}
	}
}

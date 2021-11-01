package me.kristianskaneps.bukkitwebintegration.config;

import org.bukkit.configuration.file.FileConfiguration;

public class AuthConfig
{
	public final boolean enabled, mandatory_register, mandatory_login;

	protected AuthConfig(FileConfiguration config)
	{
		enabled = config.getBoolean("auth.enabled");
		mandatory_register = config.getBoolean("auth.mandatory_register");
		mandatory_login = config.getBoolean("auth.mandatory_login");
	}

	public boolean isOptional()
	{
		return !enabled || (!mandatory_login && !mandatory_register);
	}
}

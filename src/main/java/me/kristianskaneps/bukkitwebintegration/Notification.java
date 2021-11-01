package me.kristianskaneps.bukkitwebintegration;

public class Notification
{
	public enum Type
	{
		PLAYER_JOIN,
		PLAYER_QUIT,
		PLAYER_REGISTER,
		PLAYER_LOGIN,
		PLAYER_LOGOUT,
		PLAYER_FAILED_LOGIN
	}

	public final Type type;
	public final Object value;

	public Notification(Type type)
	{
		this(type, null);
	}

	public Notification(Type type, Object value)
	{
		this.type = type;
		this.value = value;
	}
}

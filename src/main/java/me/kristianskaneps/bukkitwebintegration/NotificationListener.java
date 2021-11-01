package me.kristianskaneps.bukkitwebintegration;

public interface NotificationListener
{
	void onNotification(Notification notification);
	Notification.Type[] getSupportedNotifications();
}

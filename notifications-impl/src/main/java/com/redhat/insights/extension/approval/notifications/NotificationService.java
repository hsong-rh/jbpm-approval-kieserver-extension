package com.redhat.insights.extension.approval.notifications;

public interface NotificationService {
	public static final String LOG_PREFIX = "APPROVAL: ";

	void send(Message message);
	
	void start(ReceivedMessageHandler... callback);
	
	void stop();
}

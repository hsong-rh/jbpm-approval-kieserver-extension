package com.redhat.insights.extension.approval.notifications;

public interface ReceivedMessageHandler {

	void onMessage(Message message);
}

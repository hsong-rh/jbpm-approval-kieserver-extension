package com.redhat.insights.extension.approval.notifications;


public interface MessageExtractor {
	boolean accept(Object rawMessage);
	
	Message extract(Object rawMessage);
	
	Integer getPriority();
}

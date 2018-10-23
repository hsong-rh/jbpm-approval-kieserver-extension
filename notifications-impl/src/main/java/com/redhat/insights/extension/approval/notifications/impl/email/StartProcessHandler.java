package com.redhat.insights.extension.approval.notifications.impl.email;

import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.kie.internal.task.api.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.insights.extension.approval.notifications.Message;
import com.redhat.insights.extension.approval.notifications.ReceivedMessageHandler;

public class StartProcessHandler implements ReceivedMessageHandler {
	private static final Logger logger = LoggerFactory.getLogger(StartProcessHandler.class);

	private UserInfo userInfo;
    
    public StartProcessHandler() {
        this.userInfo = UserDataServiceProvider.getUserInfo();
        logger.info("User info: {}", userInfo);
    }

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub

	}

}

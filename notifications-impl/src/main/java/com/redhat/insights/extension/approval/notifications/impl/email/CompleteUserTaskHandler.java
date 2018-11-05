package com.redhat.insights.extension.approval.notifications.impl.email;

import java.util.HashMap;

import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.service.ServiceRegistry;
import org.kie.internal.task.api.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.insights.extension.approval.notifications.Message;
import com.redhat.insights.extension.approval.notifications.NotificationService;
import com.redhat.insights.extension.approval.notifications.ReceivedMessageHandler;

public class CompleteUserTaskHandler implements ReceivedMessageHandler {
	private static final Logger logger = LoggerFactory.getLogger(CompleteUserTaskHandler.class);
	
	private UserInfo userInfo;
		
    public CompleteUserTaskHandler() {
        this.userInfo = UserDataServiceProvider.getUserInfo();
    }

	@Override
	public void onMessage(Message message) {
		logger.info(NotificationService.LOG_PREFIX+"Received message: {}", message);
		
		long processInstanceId = message.getProcessInstanceId();
		String signalReference = message.getSignalReference();

		logger.info(NotificationService.LOG_PREFIX+"About to send signal to process instance with id {}", processInstanceId);		
		ProcessService processService = (ProcessService) ServiceRegistry.get().service(ServiceRegistry.PROCESS_SERVICE);
		
		processService.signalProcessInstance(processInstanceId, signalReference, message.getData());
		logger.info(NotificationService.LOG_PREFIX+"Signal event is sent to process instance {}, data {}", processInstanceId, message.getData());
	}

}

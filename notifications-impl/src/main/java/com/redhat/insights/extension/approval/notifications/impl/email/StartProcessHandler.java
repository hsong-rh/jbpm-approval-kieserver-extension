package com.redhat.insights.extension.approval.notifications.impl.email;

import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.kie.internal.task.api.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.insights.extension.approval.notifications.Message;
import com.redhat.insights.extension.approval.notifications.NotificationService;
import com.redhat.insights.extension.approval.notifications.ReceivedMessageHandler;

public class StartProcessHandler implements ReceivedMessageHandler {
	private static final Logger logger = LoggerFactory.getLogger(StartProcessHandler.class);

	private UserInfo userInfo;
    
    public StartProcessHandler() {
        this.userInfo = UserDataServiceProvider.getUserInfo();
        logger.info(NotificationService.LOG_PREFIX+"User info: {}", userInfo);
    }

    /*
     * Message: approval request from outside rest service
     * @see com.redhat.insights.extension.approval.notifications.ReceivedMessageHandler#onMessage(com.redhat.insights.extension.approval.notifications.Message)
     */
	@Override
	public void onMessage(Message message) {
		/*
		 * Message format: 
		 * messageId: 
		 * containerId: ex. assistant
		 * processId: ex. Approval
		 * processInstanceId:
		 * data: request content
		 */
		logger.info(NotificationService.LOG_PREFIX+"Received message: {}", message);
		/*
		String messageId = message.getMessageId();// refers to In-Reply-To
		logger.debug(NotificationService.LOG_PREFIX+"Received message with id {} is {}", messageId, message);
		
		if (!messageId.trim().isEmpty()) {
			logger.debug(NotificationService.LOG_PREFIX+"Skiping start process callback as the message is a reply");
		    return;
		}

		String containerId;
        String processId;
		
		String[] subjectSplit = message.getSubject().split(":");
			
		if (subjectSplit.length != 2) {
		    logger.debug(NotificationService.LOG_PREFIX+"Subject " + message.getSubject() + " has invalid format, quiting");
		    return;
		}
		containerId = subjectSplit[0];
        processId = subjectSplit[1];
		
		String userId = userInfo.getEntityForEmail(message.getSender());
		Map<String, Object> parameters = message.getData();
		parameters.put("sender", userId);

        
		ProcessService processService = (ProcessService) ServiceRegistry.get().service(ServiceRegistry.PROCESS_SERVICE);
		logger.debug(NotificationService.LOG_PREFIX+"About to start process with id {} with data {} in container {}", processId, parameters, containerId);
		long processInstanceId = processService.startProcess(containerId, processId, parameters);
		logger.debug(NotificationService.LOG_PREFIX+"Process instance started with id {} for message {}", processInstanceId, message);
		*/
	}

}

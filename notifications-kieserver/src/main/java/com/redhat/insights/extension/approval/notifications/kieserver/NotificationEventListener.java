package com.redhat.insights.extension.approval.notifications.kieserver;

import org.kie.server.services.api.KieContainerInstance;
import org.kie.server.services.api.KieServer;
import org.kie.server.services.api.KieServerEventListener;
import org.kie.server.services.api.KieServerExtension;
import org.kie.server.services.impl.KieServerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.insights.extension.approval.notifications.NotificationService;

public class NotificationEventListener implements KieServerEventListener {
	private static final Logger logger = LoggerFactory.getLogger(NotificationEventListener.class);

	@Override
	public void afterContainerStarted(KieServer arg0, KieContainerInstance arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterContainerStopped(KieServer arg0, KieContainerInstance arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterServerStarted(KieServer kieServer) {
        KieServerExtension notificationExtension = ((KieServerImpl)kieServer).getServerRegistry().getServerExtension(NotificationExtension.EXTENSION_NAME);
        logger.debug(NotificationService.LOG_PREFIX+"Extension: {}", notificationExtension);
        ((NotificationExtension) notificationExtension).startNotificationService();
	}

	@Override
	public void afterServerStopped(KieServer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeContainerStarted(KieServer arg0, KieContainerInstance arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeContainerStopped(KieServer arg0, KieContainerInstance arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeServerStarted(KieServer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeServerStopped(KieServer arg0) {
		// TODO Auto-generated method stub

	}

}

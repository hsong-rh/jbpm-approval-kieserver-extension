package com.redhat.insights.extension.approval.notifications.impl.email;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.activation.MimeType;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Part;

import org.jbpm.document.Document;
import org.jbpm.document.service.impl.DocumentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.insights.extension.approval.notifications.Message;
import com.redhat.insights.extension.approval.notifications.NotificationService;
import com.redhat.insights.extension.approval.notifications.impl.MessageImpl;

public class EmailMessageExtractor extends AbstractEmailMessageExtractor {

	private List<String> supportedContentTypes = Arrays.asList("multipart/alternative", "multipart/mixed");
	private static final Logger logger = LoggerFactory.getLogger(EmailMessageExtractor.class);

	@Override
	public boolean accept(Object rawMessage) {
		logger.info(NotificationService.LOG_PREFIX+" accept is called.");
		if (rawMessage instanceof javax.mail.Message) {
			javax.mail.Message source = (javax.mail.Message) rawMessage;
			boolean accepted = supports(source);
			if (accepted) {
				
				try {
					Multipart multipartContent = (Multipart) source.getContent();
					
					int numberOfParts = multipartContent.getCount();
					
					for (int i = 0; i < numberOfParts; i++) {
						BodyPart part = multipartContent.getBodyPart(i);
						MimeType mimeType = new MimeType(part.getContentType());
						if (mimeType.getBaseType().equals("text/plain")) {
							return true;
						}
					}
				} catch (Exception e) {
					logger.warn("Unexpected exception while reading message body parts", e);
				}
			}
		}
		return false;
	}

	@Override
	public Message extract(Object rawMessage) {
		logger.info(NotificationService.LOG_PREFIX + " extract is called.");

		try {
			javax.mail.Message source = (javax.mail.Message) rawMessage;

			MessageImpl message = new MessageImpl();
			message.setSubject(source.getSubject());

			String content = "";
			long processInstanceId = -1L;
			String signalReference = "";
			
			Multipart multipartContent = (Multipart) source.getContent();
			try {
				int numberOfParts = multipartContent.getCount();

				for (int i = 0; i < numberOfParts; i++) {
					BodyPart part = multipartContent.getBodyPart(i);
					MimeType mimeType = new MimeType(part.getContentType());
					if (mimeType.getBaseType().equals("text/plain")) {
						content = part.getContent().toString();
						break;
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Failed at reading message content", e);
			}

			// Skip embedded original messages
			StringBuilder trimmedContent = new StringBuilder();
			Scanner scanner = new Scanner(content);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.startsWith(">")) {
					break;
				}
				
				if (line.startsWith("ProcessInstanceId=")) {
					String[] parts = line.split("=");
					processInstanceId = Long.parseLong(parts[1]);
					message.setProcessInstanceId(processInstanceId);
					logger.info(NotificationService.LOG_PREFIX+" Process instance id: {}", processInstanceId);
				}
				
				if (line.startsWith("SignalReference=")) {
					String[] parts = line.split("=");
					signalReference = parts[1];
					message.setSignalReference(signalReference);
					logger.info(NotificationService.LOG_PREFIX+" Signal reference: {}", signalReference);					
				}
				trimmedContent.append(line).append("\n");
			}
			scanner.close();
			
			// TODO: parse message to retrieve processInstanceId and signalReference

			Map<String, Object> data = new HashMap<>();
			data.put("messageContent", trimmedContent.toString());

			List<Document> attachments = retrieveAttachments(multipartContent);
			for (Document doc : attachments) {
				data.put(doc.getName(), doc);
			}
			if (attachments.size() == 1) {
				data.put("attachment", attachments.get(0));
			} else if (!attachments.isEmpty()) {
				data.put("attachments", attachments);
			}
			message.setData(data);

			message.setContent(content);
			Address[] senders = source.getFrom();
			if (senders == null || senders.length == 0) {
				throw new IllegalArgumentException("Message does not have sender, ignoring");
			}

			String sender = senders[0].toString();
			
//			List<String> approverList = new java.util.ArrayList<String>();
//			approverList.add("hxs68000@gmail.com");
//			approverList.add("hxs6800@gmail.com");
			
//			data.put("approverList", approverList);
			data.put("Approver", sender);
			data.put("Action", "Approved");

			message.setSender(extract(sender));

			logger.debug("APPROVAL: {}", message);
			logger.debug("APPROVAL: {}", sender);
			return message;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Integer getPriority() {
		return 10;
	}

	@Override
	public List<String> getSupportedContentTypes() {
		return supportedContentTypes;
	}

	protected List<Document> retrieveAttachments(Multipart multipartContent) {
		List<Document> attachments = new ArrayList<>();
		try {
			int numberOfParts = multipartContent.getCount();
			
			for (int i = 0; i < numberOfParts; i++) {
				BodyPart part = multipartContent.getBodyPart(i);

				if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || Part.INLINE.equalsIgnoreCase(part.getDisposition())) {

					InputStream is = part.getInputStream();

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buf = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buf)) != -1) {
						bos.write(buf, 0, bytesRead);
					}
					bos.close();

					byte[] content = bos.toByteArray();
					DocumentImpl doc = new DocumentImpl(UUID.randomUUID().toString(), part.getFileName(),
							content.length, new Date());
					doc.setContent(content);
					attachments.add(doc);
				}
			}
		} catch (Exception e) {
			logger.warn("Error when reading attachments", e);
		}
		
		return attachments;
	}

}

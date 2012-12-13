package musi.interfaces.mq;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import musi.interfaces.jms.MessageService;

public class MQAdapter {
	String destinationQueue;
	String replyToQueue;
	String sourceSystem;	
	
	MessageService messageService;

	Logger log = Logger.getLogger(getClass());
	
	public void writeMessage(String message) throws IOException {					
		messageService.sendMessage(message, destinationQueue, sourceSystem, replyToQueue, true);
	}
	
	public String readMessage() throws IOException {	
		String messageBody = null;		
		Message message;
		
		try {
			message = messageService.readMessage(replyToQueue);

			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				messageBody = textMessage.getText();
				if (messageBody.isEmpty() || messageBody == null) {
					log.warn("No message available");
					return null;
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return messageBody;
	}
	
	public String readMessage(long timeout) throws IOException {	
		String messageBody = null;		
		Message message;
		
		try {
			message = messageService.readMessage(replyToQueue, Long.valueOf(timeout));
			
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				messageBody = textMessage.getText();
				if (messageBody.isEmpty() || messageBody == null) {
					log.warn("No message available");
					return null;
				}
			}	
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return messageBody;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public void setDestinationQueue(String destinationQueue) {
		this.destinationQueue = destinationQueue;
	}

	public void setReplyToQueue(String replyToQueue) {
		this.replyToQueue = replyToQueue;
	}
	
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}
}

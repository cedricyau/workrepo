package musi.interfaces.jms;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * A service that sends and receives JMS messages.
 * 
 */
public interface MessageService {

	/**
	 * Sends a message to a given queue.
	 * 
	 * @param message
	 *            Message text
	 * @param destination
	 *            destination queue where we need to send the message
	 * 
	 */
	void sendMessage(final String message, final String destination);
	
	/**
	 * Sends a message to a given queue.
	 * 
	 * @param message
	 *            Message text
	 * @param destination
	 *            destination queue where we need to send the message
	 * @param requestId used for request-reply scenario
	 * 
	 */
	void sendMessage(final String message, final String destination, String requestId);
	
	/**
	 * Sends a message to a given queue.
	 * 
	 * @param message
	 *            Message text
	 * @param destination
	 *            destination queue where we need to send the message
	 * @param source
	 * 			  source queue where the message origination
	 * @param useSource
	 * 			  set source property in message
	 * 
	 */
	void sendMessage(final String message, final String destination, String source, boolean useSource);
	
	/**
	 * Sends a message to a given queue.
	 * 
	 * @param message
	 *            Message text
	 * @param destination
	 *            destination queue where we need to send the message
	 * @param source
	 * 			  source queue where the message origination
	 * @param useSource
	 * 			  set source property in message
	 * 
	 */
	void sendMessage(final String message, final String destination, String source, final String replyToQueue, boolean useSource);
	
	/**
	 * Receives a message from a given queue.
	 * 
	 * @param source
	 *            source queue from which we need to read message
	 * @return Message
	 * 
	 * @throws JMSException
	 */
	Message readMessage(final String source) throws JMSException;
	
	/**
	 * Receives a message from a given queue.
	 * 
	 * @param source
	 *            source queue from which we need to read message
	 * @param timeout
	 * 			timeout for app to wait for message
	 * @return Message
	 * 
	 * @throws JMSException
	 */
	Message readMessage(final String source, final long timeout) throws JMSException;
}
package musi.interfaces.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.ibm.mq.jms.MQQueue;

public class MessageServiceImpl implements MessageService {	
	private JmsTemplate jmsTemplate;

	public Message readMessage(final String source) throws JMSException {
		Message msg = jmsTemplate.receive(source);
		return msg;
	}
	
	public Message readMessage(final String source, final long timeout) throws JMSException {
		jmsTemplate.setReceiveTimeout(timeout);
		Message msg = jmsTemplate.receive(source);
		return msg;
	}
	
	public void sendMessage(final String text, final String destination) {

		jmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(text);
			}
		});
	}

	public void sendMessage(final String text, String destination, final String requestId) {
		jmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message  message = session.createTextMessage(text);
				message.setJMSCorrelationID(requestId);
				return message;
			}
		});
		
	}
	
	public void sendMessage(final String text, String destination, final String source, final boolean useSource) {
		jmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message  message = session.createTextMessage(text);
				if (useSource) {
					message.setStringProperty("MUSI_MW_Source", source);
					message.setStringProperty("MUSI_MW_RemoveRFH2Header", "TRUE");
				}
				return message;
			}
		});
		
	}
	
	public void sendMessage(final String text, String destination, final String source, final String replyToQueue, final boolean useSource) {
		jmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message  message = session.createTextMessage(text);
				if (useSource) {
					message.setStringProperty("MUSI_MW_Source", source);
					message.setStringProperty("MUSI_MW_RemoveRFH2Header", "TRUE");
					message.setJMSReplyTo((Destination) new MQQueue(replyToQueue));
				}
				return message;
			}
		});
		
	}
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
}

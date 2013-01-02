package musi.interfaces.bsu.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class MessageListener implements javax.jms.MessageListener {

	public void onMessage(Message msg) {
		if (msg instanceof TextMessage) {
			try {
				String message = ((TextMessage) msg).getText();
				System.out.println("received message : " + message);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}

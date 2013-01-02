package musi.interfaces.bsu.exception;

public class MessageProcessingException extends RuntimeException {

	private static final long serialVersionUID = 7886728578612699757L;

	public MessageProcessingException() {
		super();
	}

	public MessageProcessingException(String message) {
		super(message);
	}

	public MessageProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageProcessingException(Throwable cause) {
		super(cause);
	}

}

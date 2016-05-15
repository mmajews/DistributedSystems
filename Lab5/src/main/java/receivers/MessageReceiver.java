package receivers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class MessageReceiver extends ReceiverAdapter {
	private static final Logger logger = LogManager.getLogger(MessageReceiver.class);
	private final String nickname;
	private final String channelName;

	public MessageReceiver(String channelName, String nickname) {
		this.nickname = nickname;
		this.channelName = channelName;
	}

	@Override public void receive(Message msg) {
		String senderName = msg.getSrc().toString();
		String messageAsString = (String) msg.getObject();

		if (!senderName.equals(nickname)) {
			logger.info("Received message from: {} at channel: {} with text: \"{}\"", senderName,channelName, messageAsString);
		}
	}
}

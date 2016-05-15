import core.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgroups.JChannel;
import org.jgroups.Message;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;
import receivers.MessageReceiver;
import receivers.StateReceiver;
import utils.JChannelUtils;
import utils.MessageUtils;

import java.util.HashMap;

public class Chat {
	private static final Logger logger = LogManager.getLogger(Chat.class);
	private static final String MNG_CHANNEL_ADDRESS = "228.8.8.8";
	private static final String MNG_CHANNEL_NAME = "ChannelManagement321123";
	private static final String CHANNEL_BASE_MCAST_ADDRESS = "230.0.0.";

	private final String nickname;
	private final HashMap<String, JChannel> userChannels;
	private final JChannel managementChannel;
	private final State state;

	public Chat(String nickname, State state) {
		logger.debug("Creating chat for nickname: {}", nickname);
		this.state = state;
		this.nickname = nickname;
		this.userChannels = new HashMap<>();
		managementChannel = initializeManagementChannel();
	}

	private JChannel initializeManagementChannel() {
		logger.info("Initializing management channel with name: {} and address: {}", MNG_CHANNEL_NAME, MNG_CHANNEL_ADDRESS);
		final StateReceiver receiver = new StateReceiver(state);
		return JChannelUtils.initializeChannel(nickname, MNG_CHANNEL_NAME, receiver, MNG_CHANNEL_ADDRESS);
	}

	public void connectToChannel(String channelName) {
		logger.info("Connecting to channel: {}", channelName);
		try {
			JChannel channel = JChannelUtils.initializeChannel(nickname, channelName, new MessageReceiver(channelName, nickname), CHANNEL_BASE_MCAST_ADDRESS + channelName);
			userChannels.put(channelName, channel);
			managementChannel.send(MessageUtils.createActionMessage(nickname, channelName, ChatOperationProtos.ChatAction.ActionType.JOIN));
		} catch (Exception e) {
			logger.error("Error while connecting to channel: {}", channelName);
		}
		logger.info("Connected to channel: {}", channelName);
	}

	public void sendMessageToChannel(String message, String channelName) throws Exception {
		if (userChannels.containsKey(channelName)) {
			JChannel channel = userChannels.get(channelName);
			Message msg = MessageUtils.createObjectMessage(message);
			channel.send(msg);
		} else {
			connectToChannel(channelName);
			sendMessageToChannel(message, channelName);
		}
	}

	public void leave() throws Exception {
		for (String userChannel : userChannels.keySet()) {
			managementChannel.send(MessageUtils.createActionMessage(nickname, userChannel, ChatOperationProtos.ChatAction.ActionType.LEAVE));
			userChannels.get(userChannel).close();
		}
		managementChannel.close();
	}

	public void leaveChannel(String channelName) {
		userChannels.keySet().stream().filter(userChannel -> userChannel.equals(channelName)).forEach(userChannel -> {
			try {
				managementChannel.send(MessageUtils.createActionMessage(nickname, userChannel, ChatOperationProtos.ChatAction.ActionType.LEAVE));
			} catch (Exception e) {
				e.printStackTrace();
			}
			userChannels.get(userChannel).close();
		});
	}
}

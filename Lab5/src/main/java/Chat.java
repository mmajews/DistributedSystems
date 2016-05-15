import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgroups.JChannel;

import java.util.HashMap;

public class Chat {
	private static final Logger logger = LogManager.getLogger(Chat.class);
	private static final String MANAGEMENT_CHANNEL_ADDRESS = "228.8.8.8";
	private static final String MANAGEMENT_CHANNEL_NAME = "ChannelManagement321123";
	private static final String CHANNEL_BASE_MCAST_ADDRESS = "230.0.0.";

	private final String nickname;
	private final HashMap<String, JChannel> userChannels;

	public Chat(String nickname) {
		logger.debug("Creating chat for nickname: {}", nickname);
		this.nickname = nickname;
		this.userChannels = new HashMap<>();
		initializeManagementChannel();
	}

	private void initializeManagementChannel() {
		logger.info("Initializing management channel with name: {} and address: {}", MANAGEMENT_CHANNEL_NAME,
				MANAGEMENT_CHANNEL_ADDRESS);
		JChannel jChannel = JChannelUtils.initializeChannel(nickname,MANAGEMENT_CHANNEL_NAME,new StateHandler(), MANAGEMENT_CHANNEL_ADDRESS);
	}
}

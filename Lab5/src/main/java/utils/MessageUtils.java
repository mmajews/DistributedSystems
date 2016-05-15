package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgroups.Message;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

public class MessageUtils {
	private static final Logger logger = LogManager.getLogger(MessageUtils.class);

	public static Message createActionMessage(String nickname, String channelName, ChatOperationProtos.ChatAction.ActionType actionType) {
		logger.debug("Creating message nickname: {}, channelName: {}, actionType:{}", nickname,channelName,actionType.toString());
		ChatOperationProtos.ChatAction action = ChatOperationProtos.ChatAction.newBuilder()
				.setNickname(nickname)
				.setChannel(channelName)
				.setAction(actionType)
				.build();
		return createObjectMessage(action);
	}

	public static Message createObjectMessage(Object messageObj) {
		Message msg = new Message();
		msg.setObject(messageObj);
		return msg;
	}
}

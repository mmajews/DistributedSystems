package receivers;

import core.ChannelWithUsers;
import core.State;
import core.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class StateReceiver extends ReceiverAdapter {
	private State chatState;
	private static final Logger logger = LogManager.getLogger(StateReceiver.class);

	public StateReceiver(State state) {
		this.chatState = state;
	}

	@Override
	public void receive(Message msg) {
		updateChatStateFromChatAction((ChatOperationProtos.ChatAction) msg.getObject());
	}

	@Override
	public void viewAccepted(View newView) {
		super.viewAccepted(newView);
		logger.info("Users available in cluster: {}", newView);
		chatState.updateConnectedUsers(getUsersFromView(newView));
	}

	@Override
	public void getState(OutputStream output) throws Exception {
		List<ChatOperationProtos.ChatAction> actionList = new CopyOnWriteArrayList<>();
		List<ChannelWithUsers> activeChannelsWithUsers = this.chatState.getChatState();

		for(ChannelWithUsers channel : activeChannelsWithUsers) {
			List<User> usersInChannel = channel.getUsers();
			for(User user : usersInChannel) {
				actionList.add(ChatOperationProtos.ChatAction.newBuilder()
						.setNickname(user.getName())
						.setChannel(channel.getName())
						.setAction(ChatOperationProtos.ChatAction.ActionType.JOIN)
						.build());
			}
		}
		Util.objectToStream(ChatOperationProtos.ChatState.newBuilder().addAllState(actionList).build(), new DataOutputStream(output));
	}

	@Override
	public void setState(InputStream input) throws Exception {
		List<ChannelWithUsers> newChannelList = new CopyOnWriteArrayList<>();
		ChatOperationProtos.ChatState state = (ChatOperationProtos.ChatState) Util.objectFromStream(new DataInputStream(input));

		for (ChatOperationProtos.ChatAction action : state.getStateList()) {
			String channelName = action.getChannel();
			String nickname = action.getNickname();
			if (action.getAction() == ChatOperationProtos.ChatAction.ActionType.JOIN) {
				ChannelWithUsers channel = findChannelByName(newChannelList, channelName);
				if(channel != null) {
					channel.addUser(nickname);
				} else {
					ChannelWithUsers newChannel = new ChannelWithUsers(channelName);
					newChannel.addUser(nickname);
					newChannelList.add(newChannel);
				}
			}

		}
		chatState.update(newChannelList);
	}

	private ChannelWithUsers findChannelByName(List<ChannelWithUsers> newChannelList, String channelName) {
		for(ChannelWithUsers channel : newChannelList) {
			if(channel.getName().equals(channelName)) {
				return channel;
			}
		}
		return null;
	}

	private List<String> getUsersFromView(View view) {
		return view
				.getMembers()
				.stream()
				.map(Object::toString)
				.collect(Collectors.toCollection(CopyOnWriteArrayList::new));
	}

	private void updateChatStateFromChatAction(ChatOperationProtos.ChatAction action) {
		if (action.getAction() == ChatOperationProtos.ChatAction.ActionType.JOIN) {
			chatState.addNewUserToChannel(action.getNickname(), action.getChannel());
		} else if (action.getAction() == ChatOperationProtos.ChatAction.ActionType.LEAVE) {
			chatState.deleteUserFromChannel(action.getNickname(), action.getChannel());
		}
	}
}

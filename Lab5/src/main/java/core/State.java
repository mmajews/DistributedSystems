package core;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class State {
	private List<ChannelWithUsers> channels;
	private Set<String> connectedUsers;

	public State() {
		this.channels = new CopyOnWriteArrayList<>();
		this.connectedUsers = Collections.synchronizedSet(new HashSet<>());
	}

	public List<ChannelWithUsers> getChatState() {
		return this.channels;
	}

	public void update(List<ChannelWithUsers> newChannelList) {
		this.channels.clear();
		this.channels.addAll(newChannelList);
	}

	public void addNewUserToChannel(String nickname, String channelName) {
		if(channelExists(channelName)) {
			for(ChannelWithUsers channel : channels) {
				if(channel.getName().equals(channelName)) {
					channel.addUser(nickname);
				}
			}
		} else {
			ChannelWithUsers newChannel = new ChannelWithUsers(channelName);
			newChannel.addUser(nickname);
			channels.add(newChannel);
		}
	}

	public void deleteUserFromChannel(String nickname, String channelName) {
		for (ChannelWithUsers channel : channels) {
			if(channel.getName().equals(channelName)) {
				channel.removeUser(nickname);
			}
		}
	}

	public void updateConnectedUsers(List<String> currentNicknames) {
		this.connectedUsers.clear();
		this.connectedUsers.addAll(currentNicknames);

		for (ChannelWithUsers channel : channels) {
			channel.getUsers().retainAll(this.connectedUsers);
		}
		for (ChannelWithUsers channel : channels) {
			if (channel.getUsers().isEmpty()) {
				channels.remove(channel);
			}
		}
	}

	@Override
	public String toString() {
		String stateAsString = "";
		stateAsString += "Currently available channels:\n";
		for(ChannelWithUsers channel : channels) {
			stateAsString += "- " + channel.getName() + ": ";
			for(User user : channel.getUsers()) {
				stateAsString += user.getName() + " ";
			}
			stateAsString += "\n";
		}
		return stateAsString;
	}

	private boolean channelExists(String channelName) {
		for(ChannelWithUsers channel : channels) {
			if(channel.getName().equals(channelName)) return true;
		}
		return false;
	}
}
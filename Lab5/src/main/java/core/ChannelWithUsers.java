package core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChannelWithUsers {
	private List<User> users;
	private String name;

	public ChannelWithUsers(String name) {
		this.users = new CopyOnWriteArrayList<>();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void removeUser(String nickname) {
		for (User user : users) {
			if (user.getName().equals(nickname)) {
				users.remove(user);
			}
		}
	}

	public void addUser(String nickname) {
		this.users.add(new User(nickname));
	}

	@Override
	public String toString() {
		//todo
		return "";
	}
}

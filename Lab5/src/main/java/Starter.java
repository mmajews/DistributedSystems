import core.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.Scanner;

class Starter{
	private final Scanner scanner = new Scanner(System.in);
	private static final Logger logger = LogManager.getLogger(Starter.class);
	private Chat chat;
	private State state;

	public void initialize(){
		System.out.println("Enter your name: ");
		String enteredName = scanner.nextLine();
		logger.trace("Entered name: {}", enteredName);
		state = new State();
		chat = new Chat(enteredName, state);
		printOptions();
		try {
			appLoop();
		} catch (Exception e) {
			logger.error("Unexpected problem occured",e);
		}
	}


	private void printOptions(){
		System.out.println("Main commands: ");
		System.out.println("- helpme \t\t\t\t\t prints help message");
		System.out.println("- list \t\t\t\t\t\t lists currently available channels with users logged into them");
		System.out.println("- connect [channel] \t\t connects user to channel, where channel has to be a number in range [1;200]");
		System.out.println("- send [channel] [message] \t sends message to provided channel");
		System.out.println("- leave [channel] \t\t\t leaves channel passed as argument");
		System.out.println("- exit or quit \t\t\t\t closes the application");
		System.out.println();
	}


	private void appLoop() throws Exception {
		while (true) {
			System.out.print("~ ");
			System.out.flush();
			String line = scanner.nextLine();
			if (line.equals("helpme")) {
				printOptions();
			} else if (line.equals("list")) {
				System.out.println(state);
			} else if (line.startsWith("connect ")) {
				String channelName = line.split(" ")[1];
				chat.connectToChannel(channelName);
			} else if(line.startsWith("leave ")) {
				String channelName = line.split(" ")[1];
				chat.leaveChannel(channelName);
			} else if (line.startsWith("exit") || line.startsWith("\\quit")) {
				chat.leave();
				break;
			} else if (line.startsWith("send")){
				String channelName = line.split(" ")[1];
				if (channelName.matches("\\d+")) {
					chat.sendMessageToChannel(line.substring(channelName.length()), channelName);
				} else {
					System.out.println("Sending message command: \\send [channel] [message]");
				}
			} else {
				printOptions();
			}
		}
	}
}
import core.State;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

class Starter{
	private final Scanner scanner = new Scanner(System.in);
	private static final Logger logger = LogManager.getLogger(Starter.class);


	public void initialize(){
		System.out.println("Enter your name: ");
		String enteredName = scanner.nextLine();
		logger.trace("Entered name: {}", enteredName);
		State state = new State();
		Chat chat = new Chat(enteredName, state);
	}
}
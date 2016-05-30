import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		loggerInit();

		if (args.length < 2) {
			System.err.println("usage: port program [args ...]");
			System.exit(2);
		}
		String hostPort = args[0];
		String znode = "/znode_testowy";
        String exec[] = new String[args.length - 1];
        System.arraycopy(args, 1, exec, 0, exec.length);
        Executor executor = null;
        try {
            executor = new Executor(hostPort, znode, exec);
            Thread thread = new Thread(executor);
            thread.start();
        } catch (Exception e) {
			e.printStackTrace();
		}

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String read = scanner.nextLine();
            if (read.trim().toUpperCase().equals("LS")) {
                executor.ls();
            }
        }

	}

	private static void loggerInit() {
		ConsoleAppender console = new ConsoleAppender();
		String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger.getRootLogger().addAppender(console);
	}
}

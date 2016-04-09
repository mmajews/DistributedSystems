package solutionReceiver;

import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConsoleRunner implements CommandLineRunner {
    private Logger logger = Logger.getLogger(ConsoleRunner.class);

    @Override
    public void run(String... strings) throws Exception {
        logger.info("Program started!");
    }
}

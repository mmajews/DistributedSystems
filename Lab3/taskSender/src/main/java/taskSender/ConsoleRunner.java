package taskSender;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class ConsoleRunner implements CommandLineRunner {

    private static final int NUMBER_OF_SENDERS = 10;
    private Logger logger = Logger.getLogger(ConsoleRunner.class);

    @Autowired
    private TaskSendersService senderService;

    @Override
    public void run(String... strings) throws Exception {
        logger.info("Program started!");
        senderService.createSumSenders(NUMBER_OF_SENDERS).stream().forEach(Thread::start);
        senderService.createMultiplySenders(NUMBER_OF_SENDERS).stream().forEach(Thread::start);
        senderService.createDivideSenders(NUMBER_OF_SENDERS).stream().forEach(Thread::start);
    }
}

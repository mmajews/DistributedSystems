package taskSender;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class MyRunner implements CommandLineRunner {

    private Logger logger = Logger.getLogger(MyRunner.class);

    @Autowired
    private TaskSendersService senderService;


    @Override
    public void run(String... strings) throws Exception {
        logger.info("Program started!");
        senderService.createSenders(10).stream().forEach(Thread::start);
    }
}

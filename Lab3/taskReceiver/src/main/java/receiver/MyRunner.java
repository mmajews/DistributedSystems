package receiver;

import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyRunner implements CommandLineRunner {

    private Logger logger = Logger.getLogger(MyRunner.class);


    @Override
    public void run(String... strings) throws Exception {
        logger.info("Program started!");
    }
}

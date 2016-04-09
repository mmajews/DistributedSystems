package solutionReceiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class SolutionReceiverApplication {

    public static void main(String[] args) {
        SpringApplication.run(SolutionReceiverApplication.class,args);
    }
}

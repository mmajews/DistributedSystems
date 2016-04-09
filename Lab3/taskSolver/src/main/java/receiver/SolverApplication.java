package receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class SolverApplication {

    public static void main(String[] args) {
       SpringApplication.run(SolverApplication.class, args);
    }
}

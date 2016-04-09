package receiver;

import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;


@Component
public class Listener {

    @JmsListener(destination = "sum")
    public void onMessage(String message) {
        System.out.println(message);
    }
}

package receiver;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;


@Component
public class Listener {

    @JmsListener(destination = "sum")
    public void onMessage(String message) {
        System.out.println(message);
    }

}

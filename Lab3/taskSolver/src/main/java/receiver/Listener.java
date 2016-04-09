package receiver;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.stereotype.Component;


@Component
public class Listener implements JmsListenerConfigurer {
    private final static int numberOfSolvers = 10;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar jmsListenerEndpointRegistrar) {
        for (int i = 0; i < numberOfSolvers; i++) {
            SimpleJmsListenerEndpoint newEndpoint = new SimpleJmsListenerEndpoint();
            newEndpoint.setId(String.valueOf(i));
            newEndpoint.setMessageListener(new Solver(i));
            newEndpoint.setDestination("solve");
            jmsListenerEndpointRegistrar.registerEndpoint(newEndpoint);
        }
    }

}

package solutionReceiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class Listener implements JmsListenerConfigurer {
    private final static int numberOfTopicReceivers = 10;

    @Autowired
    private JmsListenerContainerFactory jmsListenerContainerFactory;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar endpointRegistrar) {
        String[] topics = new String[]{"multiply","add","divide"};
        for(int i = 0; i<numberOfTopicReceivers;i++){
            SimpleJmsListenerEndpoint newListener = new SimpleJmsListenerEndpoint();
            newListener.setMessageListener(new SolutionListener(topics[i%3]));
            newListener.setId(String.valueOf(i));
            newListener.setDestination(topics[i%3]);
            endpointRegistrar.registerEndpoint(newListener);
        }
    }

}

package solutionReceiver;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.w3c.dom.Text;

import javax.jms.JMSException;
import javax.jms.TextMessage;


@Component
public class Listener implements JmsListenerConfigurer {
    private final static int numberOfTopicReceivers = 10;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar endpointRegistrar) {
        ActiveMQTopic multiplyTopic = new ActiveMQTopic("multiply");
        ActiveMQTopic addTopic = new ActiveMQTopic("add");
        ActiveMQTopic divideTopic = new ActiveMQTopic("divide");

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

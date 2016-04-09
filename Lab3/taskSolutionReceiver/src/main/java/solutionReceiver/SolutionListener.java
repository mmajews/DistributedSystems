package solutionReceiver;

import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class SolutionListener implements MessageListener {

    private static final Logger logger = Logger.getLogger(SolutionListener.class);
    private final String typeOfListener;

    public SolutionListener(String typeOflistener){
        this.typeOfListener = typeOflistener;
    }

    @Override
    public void onMessage(Message message) {
        final TextMessage receivedMessage = (TextMessage) message;
        try {
            logger.info(String.format("Listener (%s) received solution: %s",typeOfListener, receivedMessage.getText()));
        } catch (JMSException e) {
            logger.error("Error while receiving message",e);
        }
    }
}

package solutionReceiver;

import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

class SolutionListener implements MessageListener {

    private static final Logger logger = Logger.getLogger(SolutionListener.class);
    private final String typeOfListener;
    private final int id;

    SolutionListener(String typeOfListener, int id) {
        this.typeOfListener = typeOfListener;
        this.id = id;
    }

    @Override
    public void onMessage(Message message) {
        final TextMessage receivedMessage = (TextMessage) message;
        try {
            logger.info(String.format("Listener (%d:%s) received solution: %s", id, typeOfListener, receivedMessage.getText()));
        } catch (JMSException e) {
            logger.error("Error while receiving message",e);
        }
    }
}

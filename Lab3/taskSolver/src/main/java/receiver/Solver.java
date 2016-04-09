package receiver;

import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

class Solver implements MessageListener {
    private static final Logger logger = Logger.getLogger(Solver.class);
    private final int idOfSolver;

    Solver(int i) {
        idOfSolver = i;
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                String receivedMessage = ((TextMessage) message).getText();
                logger.info(String.format("Solver n%d. Received message : %s", idOfSolver, receivedMessage));
                publishSolution(receivedMessage, String.valueOf(parseAndCount(receivedMessage)));
            } catch (JMSException e) {
                logger.error("Error while reading message", e);
            }
        }
    }

    private double parseAndCount(String toParse){
        String[] parsed = toParse.split("[*/+-]");
        double firstNumber = Double.parseDouble(parsed[0]);
        double secondNumber = Double.parseDouble(parsed[2]);
        switch (parsed[1]){
            case "*":
                return firstNumber*secondNumber;
            case "/":
                return firstNumber/secondNumber;
            case "+":
                return firstNumber+secondNumber;
            //// FIXME: 09/04/16 support 0 as second parameter
        }
        return 0;
    }

    private void publishSolution(String equation, String solution){
        //FIXME
    }
}

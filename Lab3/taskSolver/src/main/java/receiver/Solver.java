package receiver;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
class Solver implements MessageListener {
    private static final Logger logger = Logger.getLogger(Solver.class);
    private  int idOfSolver;

    private JmsTemplate jmsTemplate;

    Solver(int i, JmsTemplate jmsTemplate) {
        idOfSolver = i;
        this.jmsTemplate =jmsTemplate;
    }

    public Solver() {
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                String receivedMessage = ((TextMessage) message).getText();
                logger.info(String.format("Solver n%d. Received message : %s", idOfSolver, receivedMessage));
                parseAndCount(receivedMessage);
            } catch (JMSException e) {
                logger.error("Error while reading message", e);
            }
        }
    }

    private void parseAndCount(String toParse){
        String[] parsed = toParse.split("((?<=[/+\\*])|(?=[/+\\*]))");
        double firstNumber = Double.parseDouble(parsed[0]);
        double secondNumber = Double.parseDouble(parsed[2]);
        double output =0;
        String destination = "";
        switch (parsed[1]){
            case "*":
                output= firstNumber*secondNumber;
                destination = "multiply";
            case "/":
                output= firstNumber/secondNumber;
                destination = "divide";
            case "+":
                output= firstNumber+secondNumber;
                destination = "add";
        }

        logger.info(String.format("Equation: %s=%f",toParse,output));
        //Setting for topic
        jmsTemplate.setPubSubDomain(true);
        double finalOutput = output;
        MessageCreator messageCreator = session -> session.createTextMessage(String.valueOf(finalOutput));
        jmsTemplate.send(destination,messageCreator);

    }
}

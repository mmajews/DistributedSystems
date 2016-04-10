package taskSender;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
class SenderService {

    private final Logger logger = Logger.getLogger(SenderService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    void sendToSolve(String equation){
        MessageCreator messageCreator = session -> session.createTextMessage(equation);
        jmsTemplate.setPubSubNoLocal(true);
        logger.info(String.format("Sending message with equation :%s",equation));
        jmsTemplate.send("solve",messageCreator);
    }
}

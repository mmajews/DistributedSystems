package taskSender;

import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;

import java.util.Random;


class Equation implements Runnable {
    private final static int threadWaitingTime = 10000;
    private final String equationMark;
    private SenderService senderService;
    private int idOfThread;
    private static Logger logger = Logger.getLogger(Equation.class);

    public Equation(int idOfThread, SenderService senderService, String equationMark) {
        this.senderService = senderService;
        this.idOfThread = idOfThread;
        this.equationMark = equationMark;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String equation = randomNumber() + equationMark + randomNumber();
                logger.info(String.format("Thread number :%d sending equation: %s", idOfThread, equation));
                senderService.sendSum(equation);
                Thread.sleep(threadWaitingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int randomNumber(){
        Random r = new Random();
        return r.ints(1,0,100).findFirst().getAsInt();
    }
}

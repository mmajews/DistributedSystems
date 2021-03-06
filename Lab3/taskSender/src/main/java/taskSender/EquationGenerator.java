package taskSender;

import org.apache.log4j.Logger;

import java.util.Random;


class EquationGenerator implements Runnable {
    private final static int threadWaitingTime = 10000;
    private final String equationMark;
    private final SenderService senderService;
    private final int idOfThread;
    private static final Logger logger = Logger.getLogger(EquationGenerator.class);

    EquationGenerator(int idOfThread, SenderService senderService, String equationMark) {
        this.senderService = senderService;
        this.idOfThread = idOfThread;
        this.equationMark = equationMark;
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                String equation = randomNumber() + equationMark + randomNumber();
                logger.info(String.format("Thread number :%d sending equation: %s", idOfThread, equation));
                senderService.sendToSolve(equation);
                Thread.sleep(threadWaitingTime);
            } catch (InterruptedException e) {
                logger.error("Error occurred while execution of equation generator", e);
            }
        }
    }

    private int randomNumber() {
        Random r = new Random();
        //noinspection OptionalGetWithoutIsPresent
        return r.ints(1, 1, 100).findFirst().getAsInt();
    }
}

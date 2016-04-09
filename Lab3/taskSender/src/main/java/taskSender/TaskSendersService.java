package taskSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskSendersService {
    private final static int threadWaitingTime = 1000;

    @Autowired
    private SenderService senderService;

    public List<Thread> createSenders(int numberOfSenders){
        List<Thread> threadsToBeReturned = new ArrayList<>();
        for(int i= 0;i<numberOfSenders;i++){
            int idOfThread = i;
            threadsToBeReturned.add(new Thread(() -> {
                while(true){
                    try {
                        String equation = "2+2";
                        System.out.println(String.format("Thread number :%d sending equation: %s",idOfThread, equation));
                        senderService.sendSum(equation);
                        Thread.sleep(threadWaitingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        return threadsToBeReturned;
    }

}

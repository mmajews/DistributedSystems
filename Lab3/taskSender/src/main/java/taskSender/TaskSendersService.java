package taskSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class TaskSendersService {

    @Autowired
    private SenderService senderService;

    List<Thread> createSumSenders(int numberOfSenders){
        List<Thread> threadsToBeReturned = new ArrayList<>();
        for(int i= 0;i<numberOfSenders;i++){
            threadsToBeReturned.add(new Thread(new Equation(i,senderService,"+")));
        }
        return threadsToBeReturned;
    }

    List<Thread> createMultiplySenders(int numberOfSenders){
        List<Thread> threadsToBeReturned = new ArrayList<>();
        for(int i= 0;i<numberOfSenders;i++){
            threadsToBeReturned.add(new Thread(new Equation(i,senderService,"*")));
        }
        return threadsToBeReturned;
    }

    List<Thread> createDivideSenders(int numberOfSenders){
        List<Thread> threadsToBeReturned = new ArrayList<>();
        for(int i= 0;i<numberOfSenders;i++){
            threadsToBeReturned.add(new Thread(new Equation(i,senderService,"/")));
        }
        return threadsToBeReturned;
    }


}

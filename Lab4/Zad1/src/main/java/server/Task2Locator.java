package server;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;
import impl.Echo;
import org.apache.log4j.Logger;

public class Task2Locator extends Locator{
    private static final Logger logger = Logger.getLogger(Task2Locator.class);

    public Task2Locator(String id) {
        super(id);
        logger.info(String.format("Task2Locator created with id: %s",id));
    }

    @Override
    public Object locate(Current current, LocalObjectHolder localObjectHolder) throws UserException {
        return new Echo();
    }
}

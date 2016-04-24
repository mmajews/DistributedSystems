package server.locators;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import org.apache.log4j.Logger;
import server.evictor.EvictorBase;

public class Task5Locator extends EvictorBase {
    private static final Logger logger = Logger.getLogger(Task5Locator.class);

    public Task5Locator(String id) {
        super(10);
        logger.debug(String.format("Creating Task5Locator"));
    }


    @Override
    public Object add(Current c, LocalObjectHolder cookie) {
        return null;
    }

    @Override
    public void evict(Object servant, java.lang.Object cookie) {

    }
}

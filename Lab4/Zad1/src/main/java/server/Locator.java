package server;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;
import org.apache.log4j.Logger;

public class Locator implements Ice.ServantLocator {
    private static Logger logger = Logger.getLogger(Locator.class);

    private String id;

    public Locator(String id) {
        this.id = id;
    }

    @Override
    public Object locate(Current current, LocalObjectHolder localObjectHolder) throws UserException {
        return null;
    }

    @Override
    public void finished(Current current, Object object, java.lang.Object o) throws UserException {
        logger.info("## ServantLocator1 #" + id + " .finished() ##");
    }


    @Override
    public void deactivate(String s) {
        logger.info("## ServantLocator1 #" + id + " .deactivate() ##");
    }
}

package server;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;
import org.apache.log4j.Logger;

class Task5Locator extends Locator {

    private static final Logger logger = Logger.getLogger(Task5Locator.class);

    Task5Locator(String id) {
        super(id);
        logger.debug(String.format("Creating Task5Locator with id: %s",id));
    }

    @Override
    public Object locate(Current current, LocalObjectHolder localObjectHolder) throws UserException {

        return null;
    }
}

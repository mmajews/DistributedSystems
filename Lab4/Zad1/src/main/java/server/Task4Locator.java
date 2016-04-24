package server;

import Ice.*;
import Ice.Object;
import impl.Echo;
import org.apache.log4j.Logger;

class Task4Locator extends Locator{
    private static final Logger logger = Logger.getLogger(Task4Locator.class);
    private Echo echo;

    Task4Locator(String id) {
        super(id);
    }

    @Override
    public Object locate(Current current, LocalObjectHolder localObjectHolder) throws UserException {
        if(echo==null){
            echo = new Echo();
        }
        return echo;
    }
}

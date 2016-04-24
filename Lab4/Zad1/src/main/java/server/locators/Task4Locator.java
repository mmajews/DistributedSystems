package server.locators;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;
import impl.Echo;
import org.apache.log4j.Logger;

public class Task4Locator extends server.Locator {
    private static final Logger logger = Logger.getLogger(Task4Locator.class);
    private Echo echo;

    public Task4Locator(String id) {
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

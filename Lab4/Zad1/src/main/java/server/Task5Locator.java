package server;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;

public class Task5Locator extends Locator {

    public Task5Locator(String id) {
        super(id);
    }

    @Override
    public Object locate(Current current, LocalObjectHolder localObjectHolder) throws UserException {

        return null;
    }
}

package server;

import Ice.*;
import Ice.Object;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

public class Task1Locator extends Locator{
    private static final Logger logger = Logger.getLogger(Task1Locator.class);
    private final ObjectAdapter objectAdapter;
    private static final Gson gson = new GsonBuilder().create();


    public Task1Locator(String id, ObjectAdapter objectAdapter) {
        super(id);
        this.objectAdapter = objectAdapter;
    }


    @Override
    public Object locate(Current current, LocalObjectHolder localObjectHolder) throws UserException {
        return null;
    }

}

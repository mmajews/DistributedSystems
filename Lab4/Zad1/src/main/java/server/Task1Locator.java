package server;

import Ice.*;
import Ice.Object;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import impl.Echo;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.Exception;

public class Task1Locator extends Locator {
    private static final Logger logger = Logger.getLogger(Task1Locator.class);
    private final ObjectAdapter objectAdapter;
    private static final Gson gson = new GsonBuilder().create();
    private static final String fileName = "src/main/resources/savedState.json";

    public Task1Locator(String id, ObjectAdapter objectAdapter) {
        super(id);
        this.objectAdapter = objectAdapter;
        logger.info(String.format("Task1Locator with id: %s started", id));
    }


    @Override
    public Object locate(Current current, LocalObjectHolder localObjectHolder) throws UserException {
        Object servant = objectAdapter.find(current.id);
        if (servant == null) {
            Echo echo = null;
            File file = new File(fileName);
            if (file.exists()) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
                    echo = gson.fromJson(bufferedReader, Echo.class);
                } catch (FileNotFoundException e) {
                    logger.error("Error while reading from file.. Exiting", e);
                    System.exit(1);
                }
            } else {
                logger.warn(String.format("File with path %s does not exist. New will be created", file.getAbsolutePath()));
                echo = new Echo();

            }
            Preconditions.checkNotNull(echo, "Echo must not be null!");
            objectAdapter.add(echo, current.id);
            return echo;
        } else {
            return servant;
        }
    }

}

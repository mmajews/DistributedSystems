package server.locators;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import Ice.UserException;
import com.google.common.base.Preconditions;
import impl.Echo;
import org.apache.log4j.Logger;
import server.Locator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Task3Locator extends Locator {
    private static final Logger logger = Logger.getLogger(Task3Locator.class);
    private static final int numberOfServants = 10;
    private List<Echo> servants = new ArrayList<>();
    private boolean initialized = false;

    public Task3Locator(String id) {
        super(id);
        logger.info("Task3Locator created with id " + id);
        Preconditions.checkArgument(numberOfServants > 0, "Number of servants must be more than 0");
    }

    private void initializeArray() {
        for (int i = 0; i < numberOfServants; i++) {
            servants.add(new Echo());
        }
    }

    @Override
    public Object locate(Current current, LocalObjectHolder localObjectHolder) throws UserException {
        if (!initialized) {
            initializeArray();
            initialized = true;
        }
        Random random = new Random(System.currentTimeMillis());
        final int randomElement = random.nextInt(numberOfServants);
        logger.debug(String.format("Getting random element with index %d",randomElement));
        return servants.get(randomElement);
    }
}

package server.locators;

import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.Object;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import impl.Echo;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import server.evictor.EvictorBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class Task5Locator extends EvictorBase {
    private static final Logger logger = Logger.getLogger(Task5Locator.class);
    private static final String fileName = "src/main/resources/savedEvicts.json";
    private static final Gson gson = new GsonBuilder().create();
    private String id;

    public Task5Locator(String id) {
        super(10);
        logger.debug(String.format("Task5Locator created with id: %s", id));
    }


    @Override
    public Object add(Current c, LocalObjectHolder cookie) {
        return new Echo();
    }

    @Override
    public void evict(Object servant, java.lang.Object cookie) {
        createBackupFileIfNotExists();
        try {
            final File backupFile = new File(fileName);
            readObjectAndAddNew((Echo) servant, backupFile);
        } catch (IOException e) {
            logger.error(String.format("Problem occurred while reading from file %s", fileName));
        }
    }

    private void readObjectAndAddNew(Echo servant, File backupFile) throws IOException {
        String allObjects = FileUtils.readFileToString(backupFile);
        Echo[] allEchoes = gson.fromJson(allObjects, Echo[].class);
        List<Echo> listOfAllEchoes = Arrays.asList(allEchoes);
        listOfAllEchoes.add(servant);
        String serializedToJson = gson.toJson(listOfAllEchoes);
        clearFile(backupFile);
        FileUtils.writeStringToFile(backupFile, serializedToJson);
    }

    private void createBackupFileIfNotExists() {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                boolean ifCreated = file.createNewFile();
                Preconditions.checkArgument(ifCreated, String.format("File %s not created", file.getAbsolutePath()));
            } catch (IOException e) {
                logger.error(String.format("Cannot create file: %s", file.getAbsolutePath()), e);
            }
        } else {
            clearFile(file);
        }
    }

    private void clearFile(File file) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            logger.error(String.format("Error while creating file writer for file: %s", file.getAbsolutePath()));
        }

    }

}

package de.villigst.dk.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.villigst.dk.logic.Logger;
import de.villigst.dk.model.DKMember;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContentManager {

    //Resource:
    //https://www.baeldung.com/jackson-object-mapper-tutorial

    public void saveToFile(String path) {
        try {
            File destinationFile = new File(path);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(destinationFile, Persistent.getMembers());
        }catch(IOException ex) {
            Logger.error("Failed to save state: " + ex.getMessage());
        }
    }

    public void loadFromFile(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = Files.readString(Paths.get(path));
            Persistent.setMembers(mapper.readValue(json, new TypeReference<List<DKMember>>() {}));
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void reset() {
        Persistent.setMembers(new ArrayList<>());
        Persistent.setRandomLists(new HashMap<>());
        Persistent.setRandomListsSelected(new HashMap<>());
        Persistent.setPrintSelected(new HashMap<>());
        Persistent.setNotes("");
        Persistent.setStatMG(0);
        Persistent.setStatWG(0);
        Persistent.setStatDG(0);
        Persistent.setStatMS(0);
        Persistent.setStatWS(0);
        Persistent.setStatDS(0);

    }

}

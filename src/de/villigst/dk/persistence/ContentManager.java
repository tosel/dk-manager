package de.villigst.dk.persistence;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.villigst.dk.logic.Logger;
import de.villigst.dk.model.DKMember;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ContentManager {

    public void saveToFile(String path) {
        try {
            File destinationFile = new File(path);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(destinationFile, Persistent.members);
        }catch(IOException ex) {
            Logger.error("Failed to save state: " + ex.getMessage());
        }
    }

    public void loadFromFile(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = Files.readString(Paths.get(path));
            Persistent.members = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, DKMember.class));
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

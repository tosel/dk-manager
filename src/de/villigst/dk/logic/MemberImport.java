package de.villigst.dk.logic;

import de.villigst.dk.model.DKMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MemberImport {

    @Nullable
    private static List<DKMember> importCSV(@NotNull List<String> lines) {
        Logger.info("Importiere Teilnehmende aus Datei...");
        //first line = column-definitions
        String[] definitions = lines.get(0).split(";");
        //check for right definitions
        if(!definitions[1].equalsIgnoreCase("vorname") || !definitions[2].equalsIgnoreCase("nachname") || !definitions[6].equalsIgnoreCase("Kommentar")){
            Logger.error("Wrong definition for import. Use 'Titel;Vorname;Nachname;Gliederungsebene;Teilnehmernummer;Gruppen;Kommentar;Ist aktiv;Ist anwesend;Ist Gremium;Initiales Passwort;E-Mail'.");
            return null;
        }
        //Erstelle Objekte
        List<DKMember> members = new ArrayList<>();
        for(int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] contents = line.split(";");
            String konvent = contents[2].replace("(", "").replace(")", "");
            DKMember m = new DKMember(contents[1], konvent, contents[6]);
            members.add(m);
        }
        return members;
    }

    @Nullable
    public static List<DKMember> importCSVFile(@NotNull String csvFilePath) {
        Logger.info("Lese Datei ein...");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
            List<String> lines = new ArrayList<>();
            //Schreibe jede Zeile der Datei in ein List-Item
            String line = reader.readLine();
            while(line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
            return importCSV(lines);
        }catch(IOException ex) {
            Logger.error("Import failed: " + ex.getMessage());
            return null;
        }
    }



}

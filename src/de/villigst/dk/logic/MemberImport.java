package de.villigst.dk.logic;

import de.villigst.dk.model.DKMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MemberImport {

    @Nullable
    public static List<DKMember> importCSV(@NotNull String csv) {

        return null;
    }

    @Nullable
    public static List<DKMember> importCSVFile(@NotNull String csvFilePath) {
        try {
            String csvString = Files.readString(Paths.get(csvFilePath));
            return importCSV(csvString);
        }catch(IOException ex) {
            Logger.error("Import failed: " + ex.getMessage());
            return null;
        }
    }



}

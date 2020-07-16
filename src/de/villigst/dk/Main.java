package de.villigst.dk;

import de.villigst.dk.logic.Generator;
import de.villigst.dk.logic.MemberImport;
import de.villigst.dk.model.DKMember;
import de.villigst.dk.persistence.ContentManager;
import de.villigst.dk.persistence.Persistent;
import de.villigst.dk.view.ManagerUI;

import java.util.ArrayList;
import java.util.List;

public class Main {


    public static final String MEMBER_IMPORT_PATH = "C:\\Users\\topps\\OneDrive - ruhr-uni-bochum.de\\Villigst\\DKen\\HDK 2019\\Teilnehmerverwaltung\\Import OS.csv";

    public static void main(String[] args) {
        //Initialize ManagerUI
        new ManagerUI();

        //Read members
        MemberImport.importCSVFile(MEMBER_IMPORT_PATH);
        for(DKMember m : Persistent.getMembers()) {
            //System.out.println(m.toString());
        }
        //Generator.generateMeldeschilder(Persistent.members);
        //Generator.generateNamensschilder(Persistent.members);
        //Generator.generateGremienschilder(Persistent.members);
        ContentManager manager = new ContentManager();
        manager.saveToFile("out/save.json");
        Persistent.setMembers(new ArrayList<>());
        manager.loadFromFile("out/save.json");
        for(DKMember m : Persistent.getMembers()) {
            System.out.println(m.toString());
        }
    }

}

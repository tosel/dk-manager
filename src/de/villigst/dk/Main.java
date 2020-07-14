package de.villigst.dk;

import de.villigst.dk.logic.Generator;
import de.villigst.dk.logic.MemberImport;
import de.villigst.dk.logic.PDFConverter;
import de.villigst.dk.model.DKMember;
import de.villigst.dk.view.ManagerUI;

import java.util.List;

public class Main {

    public static final String TEMPLATE_MELDESCHILD_PATH = "src/de/villigst/dk/template/meldeschild_a4.html";
    public static final String OUTPUT_PDF_PATH = "out/out.pdf";
    public static final String OUTPUT_PATH = "out/";
    public static final String MEMBER_IMPORT_PATH = "C:\\Users\\topps\\OneDrive - ruhr-uni-bochum.de\\Villigst\\DKen\\HDK 2019\\Teilnehmerverwaltung\\Import OS.csv";

    public static void main(String[] args) {
        //Initialize ManagerUI
        //new ManagerUI();
        //PDFConverter converter = new PDFConverter(TEMPLATE_MELDESCHILD_PATH, OUTPUT_PDF_PATH);
        //converter.convert(true);

        List<DKMember> members = MemberImport.importCSVFile(MEMBER_IMPORT_PATH);
        for(DKMember m : members) {
            //System.out.println(m.toString());
        }
        Generator.generateMeldeschilder(members);
        Generator.generateNamensschilder(members);
        Generator.generateGremienschilder(members);
    }

}

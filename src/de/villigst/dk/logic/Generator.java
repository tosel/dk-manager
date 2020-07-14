package de.villigst.dk.logic;

import de.villigst.dk.Main;
import de.villigst.dk.model.DKMember;
import de.villigst.dk.persistence.Persistent;
import de.villigst.dk.template.TemplateManager;

import java.util.ArrayList;
import java.util.List;

public class Generator {

    /***
     * Generiert ein PDF mit den Hochhalteschildern zum Melden für alle DK Teilnehmer
     * @param members Alle Teilnehmenden der DK
     */
    public static void generateMeldeschilder(List<DKMember> members) {
        Logger.info("Generiere Meldeschilder...");
        List<String> htmls = new ArrayList<>();
        for(DKMember m : members) {
            htmls.add(TemplateManager.MELDESCHILDER.getString(m.getName(), m.getKonvent()));
        }
        PDFConverter.convert(Persistent.OUTPUT_PATH + "Meldeschilder.pdf", htmls, true);
    }

    /***
     * Generiert ein PDF mit den Tischschildern für die Gremianer*innen.
     * @param members Alle Teilnehmenden der DK. Verwendet nur die Gremianer. Prüft nach Kommentar-Zeile
     */
    public static void generateGremienschilder(List<DKMember> members) {
        Logger.info("Generiere Gremienschilder...");
        List<String> htmls = new ArrayList<>();
        for(DKMember m : members) {
            if(m.isGremium()) {
                htmls.add(TemplateManager.GREMIENSCHILD.getString(m.getName(), m.getAmt()));
            }
        }
        PDFConverter.convert(Persistent.OUTPUT_PATH + "Gremienschilder.pdf", htmls, true);
    }

    /***
     *
     * @param members Alle Teilnehmenden der DK
     */
    public static void generateNamensschilder(List<DKMember> members) {
        Logger.info("Generiere Namensschilder...");
        List<String> pages = new ArrayList<>();
        List<String> schilder = new ArrayList<>();
        for(int i = 0; i < members.size(); i++) {
            schilder.add(TemplateManager.NAMENSSCHILD_ENTITY.getString(members.get(i).getName(), members.get(i).getKonvent()));
            if((i+1) % TemplateManager.NAMENSSCHILDER_PRO_SEITE == 0) {
                pages.add(TemplateManager.NAMENSSCHILD_WRAPPER.getString(schilder));
                schilder = new ArrayList<>();
            }
        }
        pages.add(TemplateManager.NAMENSSCHILD_WRAPPER.getString(schilder));
        PDFConverter.convert(Persistent.OUTPUT_PATH + "Namensschilder.pdf", pages, false);
    }

}

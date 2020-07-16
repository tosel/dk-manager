package de.villigst.dk.logic;

import de.villigst.dk.model.DKMember;
import de.villigst.dk.template.TemplateManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Generator {

    /***
     * Generiert ein PDF mit den Hochhalteschildern zum Melden für alle DK Teilnehmer
     * @param members Alle Teilnehmenden der DK
     */
    public static void generateMeldeschilder(@NotNull List<DKMember> members, File target) {
        Logger.info("Generiere Meldeschilder...");
        List<String> htmls = new ArrayList<>();
        for(DKMember m : members) {
            htmls.add(TemplateManager.MELDESCHILDER.getString(m.getName(), m.getKonvent()));
        }
        PDFConverter.convert(getPath(target), htmls, true);
    }

    /***
     * Generiert ein PDF mit den Tischschildern für die Gremianer*innen.
     * @param members Alle Teilnehmenden der DK. Verwendet nur die Gremianer. Prüft nach Kommentar-Zeile
     */
    public static void generateGremienschilder(@NotNull List<DKMember> members, File target) {
        Logger.info("Generiere Gremienschilder...");
        List<String> htmls = new ArrayList<>();
        for(DKMember m : members) {
            if(m.isGremium()) {
                htmls.add(TemplateManager.GREMIENSCHILD.getString(m.getName(), m.getAmt()));
            }
        }
        PDFConverter.convert(getPath(target), htmls, true);
    }

    /***
     *
     * @param members Alle Teilnehmenden der DK
     */
    public static void generateNamensschilder(@NotNull List<DKMember> members, File target) {
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
        PDFConverter.convert(getPath(target), pages, false);
    }

    public static String getPath(File file) {
        if(!file.getName().endsWith(".pdf")) {
            return file.getPath() + ".pdf";
        }else {
            return file.getPath();
        }
    }

}

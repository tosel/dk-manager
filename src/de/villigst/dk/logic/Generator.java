package de.villigst.dk.logic;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import de.villigst.dk.model.DKMember;
import de.villigst.dk.template.TemplateManager;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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

    private static PdfDocument generateBSTitle(String title) {
        String date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + Calendar.getInstance().get(Calendar.MONTH) + "." + Calendar.getInstance().get(Calendar.YEAR);
        String html = TemplateManager.BS_TITLE.getString(title, date);
        return PDFConverter.convert(html, false);
    }

    public static void generateBeschlusssammlung(File target, String title, File contents, File lastBS, File newBS, int offset) {
        try {
            PdfWriter writer = new PdfWriter(getPath(target));
            PdfDocument pdfFinal = new PdfDocument(writer);
            PdfMerger merger = new PdfMerger(pdfFinal);
            PdfDocument pdfTitel = generateBSTitle(title);

            //Read Contents-File
            PdfReader readerContents = new PdfReader(contents);
            PdfDocument pdfContents = new PdfDocument(readerContents);

            //Read last BS-File
            PdfReader readerBS = new PdfReader(lastBS);
            //PdfWriter writerBS = new PdfWriter(new ByteArrayOutputStream());
            PdfDocument pdfBS = new PdfDocument(readerBS);//, writerBS);
            //Add page-numbers
            /*Document docBS = new Document(pdfBS);
            for(int page = 1; page <= pdfBS.getNumberOfPages(); page++) {
                docBS.showTextAligned(new Paragraph(String.valueOf(page)), 559, 806, page, TextAlignment.RIGHT, VerticalAlignment.TOP, 0);
            }
            writerBS.close();
            docBS.close();
            */

            //Read new BS-File
            PdfReader readerNewBS = new PdfReader(newBS);
            PdfDocument pdfNewBS = new PdfDocument(readerNewBS);


            //==== MERGE ====
            //Titel
            merger.merge(pdfTitel, 1, 1);
            //Inhaltsverzeichnis Teil 1 (statisch)
            merger.merge(pdfBS, 2, 5);
            //Inhaltsverzeichnis Teil 2
            merger.merge(pdfContents, 1, pdfContents.getNumberOfPages());
            //Alte Beschlüsse
            merger.merge(pdfBS, offset, pdfBS.getNumberOfPages());
            //Neue Beschlüsse
            merger.merge(pdfNewBS, 1, pdfNewBS.getNumberOfPages());

            //Add page numbers
            Document doc = new Document(pdfFinal);
            int startPage = pdfTitel.getNumberOfPages() + pdfContents.getNumberOfPages() + pdfBS.getNumberOfPages() + 1 - 4;
            for(int page = startPage; page <= pdfFinal.getNumberOfPages(); page++) {
                PdfPage pdfPage = pdfFinal.getPage(page);
                Rectangle pageSize = pdfPage.getCropBox();
                try {
                    Canvas canvas = new Canvas(new PdfCanvas(pdfPage, true), pdfFinal, pageSize);
                    canvas.add(new Paragraph(String.valueOf(page-pdfContents.getNumberOfPages()))
                            .setFixedPosition(page, pageSize.getWidth() / 2, 30, 50));
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
                //doc.showTextAligned(new Paragraph(),
                       // , , page, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
            }

            //==== close all ====
            pdfTitel.close();
            pdfContents.close();
            pdfBS.close();

            pdfFinal.close();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getPath(File file) {
        if(!file.getName().endsWith(".pdf")) {
            return file.getPath() + ".pdf";
        }else {
            return file.getPath();
        }
    }

}

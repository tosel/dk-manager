package de.villigst.dk.logic;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;

public class PDFConverter {

    /***
     *
     * @param pdfDestPath Pfad der Zieldatei
     * @param htmls HTML-Input - Eine Seite pro Item
     * @param landscape Orientierung des Dokumentes
     */
    public static void convert(@NotNull String pdfDestPath, @NotNull List<String> htmls, boolean landscape) {
        try {
            PdfWriter writer = new PdfWriter(pdfDestPath);
            PdfDocument pdf = new PdfDocument(writer);
            if (landscape) pdf.setDefaultPageSize(PageSize.A4.rotate());
            PdfMerger merger = new PdfMerger(pdf);
            for (String html : htmls) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument temp = new PdfDocument(new PdfWriter(baos));
                if (landscape) temp.setDefaultPageSize(PageSize.A4.rotate());
                HtmlConverter.convertToPdf(new ByteArrayInputStream(html.getBytes()), temp);
                temp = new PdfDocument(
                        new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
                merger.merge(temp, 1, temp.getNumberOfPages());
                temp.close();
            }
            pdf.close();
        }catch (IOException ex) {
            Logger.error("Error during conversion: " + ex.getMessage());
        }
    }

}

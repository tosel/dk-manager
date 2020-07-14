package de.villigst.dk.logic;

import com.itextpdf.html2pdf.HtmlConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFConverter {

    private final String htmlSrc, pdfDest;
    private File htmlSrcFile, pdfDestFile;

    public PDFConverter(String htmlSrc, String pdfDest) {
        this.htmlSrc = htmlSrc;
        this.pdfDest = pdfDest;

        File htmlSrcFile = new File(htmlSrc);


        File pdfDestFile = new File(pdfDest);
        pdfDestFile.getParentFile().mkdirs();

    }

    public File convert() {
        if(!htmlSrcFile.exists()) {
            Logger.error("Invalid source file: Not found");
            return null;
        }
        try {
            HtmlConverter.convertToPdf(new FileInputStream(htmlSrcFile), new FileOutputStream(pdfDestFile));
            return pdfDestFile;
        }catch(IOException ex) {
            Logger.error("Conversion failed: " + ex.getMessage());
            return null;
        }
    }

}

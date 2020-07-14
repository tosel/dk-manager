package de.villigst.dk.logic;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

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

        this.htmlSrcFile = new File(htmlSrc);


        this.pdfDestFile = new File(pdfDest);
        pdfDestFile.getParentFile().mkdirs();

    }

    public File convert(boolean landscape) {
        if(!htmlSrcFile.exists()) {
            Logger.error("Invalid source file: Not found");
            return null;
        }
        try {
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfDestFile));
            if(landscape) pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
            HtmlConverter.convertToPdf(new FileInputStream(htmlSrcFile), pdfDocument);
            return pdfDestFile;
        }catch(IOException ex) {
            Logger.error("Conversion failed: " + ex.getMessage());
            return null;
        }
    }

}

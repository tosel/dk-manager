package de.villigst.dk;

import de.villigst.dk.logic.PDFConverter;
import de.villigst.dk.view.ManagerUI;

public class Main {

    public static void main(String[] args) {
        //Initialize ManagerUI
        new ManagerUI();
        PDFConverter converter = new PDFConverter("C:\\Users\\topps\\OneDrive - ruhr-uni-bochum.de\\Uni\\Workspace\\DK Manager\\src\\de\\villigst\\dk\\template\\meldeschild_a4.html", "C:\\Users\\topps\\OneDrive - ruhr-uni-bochum.de\\Uni\\Workspace\\DK Manager\\out\\out.pdf");
        converter.convert(true);
    }

}

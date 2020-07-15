package de.villigst.dk.controller;

import de.villigst.dk.model.DKMember;
import de.villigst.dk.persistence.Persistent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class RandomNewListDialogController extends Dialog {

    @FXML
    TextField random_dialog_name;

    @FXML
    CheckBox random_dialog_cb_gremium, random_dialog_cb_gs, random_dialog_cb_gaeste;

    public void random_dialog_create() {
        String listname = random_dialog_name.getText();
        if(listname.length() > 0) {
            List<DKMember> selected = new ArrayList<>();
            for(DKMember m : Persistent.members) {
                if(m.isGremium() && random_dialog_cb_gremium.isSelected()) {
                    selected.add(m);
                }else if(m.isGS() && random_dialog_cb_gs.isSelected()) {
                    selected.add(m);
                }else if(m.isGuest() && random_dialog_cb_gaeste.isSelected()) {
                    selected.add(m);
                }
                //Prüfe normaler Stipi:
                if(! (m.isGuest() || m.isGS() || m.isGremium())) {
                    selected.add(m);
                }
            }
            Collections.shuffle(selected);
            Persistent.random_lists.put(listname, selected);

            //Dialog schließen
            random_dialog_name.getScene().getWindow().hide();
            //((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte gib einen Namen an!");
            alert.show();
        }
    }
}

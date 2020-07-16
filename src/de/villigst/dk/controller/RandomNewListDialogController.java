package de.villigst.dk.controller;

import de.villigst.dk.model.DKMember;
import de.villigst.dk.persistence.Persistent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RandomNewListDialogController extends Dialog {

    @FXML
    TextField random_dialog_name, random_dialog_limit;
    @FXML
    CheckBox random_dialog_cb_senat, random_dialog_cb_pa, random_dialog_cb_gs, random_dialog_cb_gaeste, random_dialog_cb_stipis;

    public void random_dialog_create() {
        if(random_dialog_cb_gaeste.isSelected() || random_dialog_cb_gs.isSelected() || random_dialog_cb_stipis.isSelected() || random_dialog_cb_pa.isSelected() || random_dialog_cb_senat.isSelected()) {
            if (random_dialog_name.getText().length() > 0) {
                if(Persistent.getRandomLists().get(random_dialog_name.getText()) == null) {
                    int limit;
                    try {
                        if(random_dialog_limit.getText().isBlank()) {
                            limit = -1;
                        }else {
                            limit = Integer.valueOf(random_dialog_limit.getText());
                        }
                    }catch(NumberFormatException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Fehler");
                        alert.setHeaderText(null);
                        alert.setContentText("Bitte gib eine Zahl als Limit an!");
                        alert.show();
                        return;
                    }
                    List<DKMember> selected = new ArrayList<>();
                    for (DKMember m : Persistent.getMembers()) {
                        if (m.isSenat() && random_dialog_cb_senat.isSelected()) {
                            selected.add(m);
                        } else if (m.isPA() && random_dialog_cb_pa.isSelected()){
                            selected.add(m);
                        }else if (m.isGS() && random_dialog_cb_gs.isSelected()) {
                            selected.add(m);
                        } else if (m.isGuest() && random_dialog_cb_gaeste.isSelected()) {
                            selected.add(m);
                        } else if(random_dialog_cb_stipis.isSelected() && !m.isGremium() && !m.isGS() && !m.isGuest()){
                            selected.add(m);
                        }
                    }
                    Collections.shuffle(selected);
                    if(limit > 0) {
                        if(limit < selected.size()) {
                            Persistent.getRandomLists().put(random_dialog_name.getText(), selected.subList(0, limit));
                            Persistent.getRandomListsSelected().put(random_dialog_name.getText(), new HashMap<>());
                        }else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Fehler");
                            alert.setHeaderText(null);
                            alert.setContentText("Das Limit muss kleiner als die Anzahl von ausgewählten Teilnehmern (" + selected.size() + ") sein!");
                            alert.show();
                            return;
                        }
                    }else {
                        Persistent.getRandomLists().put(random_dialog_name.getText(), selected);
                        Persistent.getRandomListsSelected().put(random_dialog_name.getText(), new HashMap<>());
                    }
                    //Dialog schließen
                    random_dialog_name.getScene().getWindow().hide();
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Fehler");
                    alert.setHeaderText(null);
                    alert.setContentText("Es existiert bereits eine Liste mit diesem Namen!");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText(null);
                alert.setContentText("Bitte gib einen Namen an!");
                alert.show();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte wähle mindestens eine Gruppe!");
            alert.show();
        }
    }

    public void random_dialog_manual() {
        if (random_dialog_name.getText().length() > 0) {
            //Open new Selection-Window
            try {
                URL url = new URL("file:src/de/villigst/dk/view/RandomManualSelectionDialogView.fxml");
                //Parent root = FXMLLoader.load(url);
                FXMLLoader loader = new FXMLLoader(url);
                Stage stage = new Stage();
                stage.setTitle("Teilnehmer auswählen");
                stage.setScene(new Scene(loader.load(), 1000, 600));
                stage.setResizable(false);

                RandomManualSelectionDialogController controller = loader.<RandomManualSelectionDialogController>getController();
                controller.setTitleString(random_dialog_name.getText());

                stage.showAndWait();

                random_dialog_name.getScene().getWindow().hide();
            }catch(IOException ex) {
                ex.printStackTrace();
            }

        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte gib einen Namen an!");
            alert.show();
        }
    }
}

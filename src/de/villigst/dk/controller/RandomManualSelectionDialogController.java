package de.villigst.dk.controller;

import de.villigst.dk.model.DKMember;
import de.villigst.dk.persistence.Persistent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class RandomManualSelectionDialogController extends Dialog implements Initializable {

    HashMap<String, BooleanProperty> selected = new HashMap<>();
    private String title;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        select_listview.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {

            @Override
            public ObservableValue<Boolean> call(String s) {
                if(selected.get(s) == null) {
                    selected.put(s, new SimpleBooleanProperty(false));
                }
                return selected.get(s);
            }
        }));

        select_refresh_listview();
    }

    @FXML
    TextField select_filter;
    @FXML
    ListView select_listview;

    public void select_done() {
        List<DKMember> selectedMembers = new ArrayList<>();

        for(String s : selected.keySet()) {
            if(selected.get(s).getValue()) {
                for(DKMember m : Persistent.getMembers()) {
                    if(m.getDisplay().equalsIgnoreCase(s)) {
                        selectedMembers.add(m);
                    }
                }
            }
        }

        if(!selectedMembers.isEmpty()) {

            Persistent.getRandomLists().put(title, selectedMembers);
            Persistent.getRandomListsSelected().put(title, new HashMap<>());


            select_filter.getScene().getWindow().hide();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte wähle mindestens einen Teilnehmer aus!");
            alert.show();
        }
    }

    public void select_filter_onkeytyped() {
        String filter = select_filter.getText();
        if(filter.length() > 0) {
            select_listview.getItems().clear();
            for (DKMember m : Persistent.getMembers()) {
                if (m.getDisplay().toLowerCase().contains(filter.toLowerCase())) {
                    select_listview.getItems().add(m.getDisplay());
                }
            }
        }else {
            select_refresh_listview();
        }
    }

    public void select_refresh_listview() {
        select_listview.getItems().clear();
        for(DKMember m : Persistent.getMembers()) {
            select_listview.getItems().add(m.getDisplay());
        }
    }


    public void setTitleString(String title) {
        this.title = title;
    }
}

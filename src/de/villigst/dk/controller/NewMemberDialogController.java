package de.villigst.dk.controller;

import de.villigst.dk.model.DKMember;
import de.villigst.dk.persistence.Persistent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

public class NewMemberDialogController extends Dialog {

    @FXML
    TextField member_dialog_name, member_dialog_konvent, member_dialog_amt;
    @FXML
    CheckBox member_dialog_cb_gremium;

    public void member_check_gremium() {
        if(member_dialog_cb_gremium.isSelected()) {
            member_dialog_amt.setDisable(false);
        }else {
            member_dialog_amt.setDisable(true);
        }
    }

    public void member_dialog_add() {
        if(member_dialog_name.getText().isBlank() || member_dialog_konvent.getText().isBlank() || member_dialog_cb_gremium.isSelected() && member_dialog_amt.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte fülle alle Felder aus!");
            alert.show();
        }else {
            DKMember toAdd = new DKMember(member_dialog_name.getText(), member_dialog_konvent.getText(), member_dialog_amt.getText());
            Persistent.members.add(toAdd);
            //close
            member_dialog_name.getScene().getWindow().hide();
        }
    }


}

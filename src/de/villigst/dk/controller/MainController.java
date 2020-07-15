package de.villigst.dk.controller;

import de.villigst.dk.FXMain;
import de.villigst.dk.logic.Generator;
import de.villigst.dk.logic.Logger;
import de.villigst.dk.logic.MemberImport;
import de.villigst.dk.model.DKMember;
import de.villigst.dk.persistence.Persistent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    private ObservableList<PieChart.Data> stats_chart_mwd_data;
    private ObservableList<PieChart.Data> stats_chart_gremianer_data;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Fill ListView in members with content
        refresh_members();

        //Initialize PieCharts
        stats_chart_mwd_data = FXCollections.observableArrayList(
                new PieChart.Data("Männlich", 1),
                new PieChart.Data("Weiblich", 1),
                new PieChart.Data("Divers", 1));
        stats_chart_mwd.setData(stats_chart_mwd_data);
        stats_chart_gremianer_data = FXCollections.observableArrayList(
                new PieChart.Data("Gremianer", 1),
                new PieChart.Data("Nicht-Gremianer", 1));
        stats_chart_gremianer.setData(stats_chart_gremianer_data);

        //Initialize Random ListView-Select
        random_listview_created.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
                if(newValue != null) {
                    random_listview_selected.getItems().clear();
                    for (DKMember m : Persistent.random_lists.get(newValue)) {
                        String gremium = m.isGremium() ? " [" + m.getAmt() + "]" : "";
                        String name = m.getName() + " (" + m.getKonvent() + ")" + gremium;
                        random_listview_selected.getItems().add(name);
                    }
                }
            }
        });
    }

    private void refresh_members() {
        members_refresh_listview();
        print_refresh_listview();
    }

    // ==================== Seite: ÜBERSICHT ====================

    @FXML
    TextArea overview_ta_notes;

    public void overview_reset_content() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Wirklich alle Inhalte zurücksetzen?", ButtonType.CANCEL, ButtonType.YES);
        alert.setTitle("Inhalte zurücksetzen");
        alert.setHeaderText(null);
        alert.showAndWait();
        if(alert.getResult() == ButtonType.YES) {
            Persistent.members = new ArrayList<>();
            Persistent.random_lists = new HashMap<>();
            Persistent.stat_mg = 0;
            Persistent.stat_wg = 0;
            Persistent.stat_dg = 0;
            Persistent.stat_ms = 0;
            Persistent.stat_ws = 0;
            Persistent.stat_ds = 0;
        }
    }



    // ==================== Seite: Teilnehmerverwaltung ====================

    @FXML
    TextField members_filter;
    @FXML
    ListView members_listview;
    @FXML
    AnchorPane members_anchor;

    public void members_csv_import(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("CSV Datei auswählen...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(FXMain.stage);
        if(selectedFile != null) {
            MemberImport.importCSVFile(selectedFile.getPath());
            refresh_members();
        }
    }

    public void members_add_manually(){
        try {
            URL url = new URL("file:src/de/villigst/dk/view/NewMemberDialogView.fxml");
            Parent root = FXMLLoader.load(url);
            Stage stage = new Stage();
            stage.setTitle("Neuen Teilnehmer hinzufügen");
            stage.setScene(new Scene(root, 300, 400));
            stage.setResizable(false);
            stage.showAndWait();

            refresh_members();
        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void members_remove_all(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Wirklich alle Teilnehmer entfernen?", ButtonType.CANCEL, ButtonType.YES);
        alert.setTitle("Alle Teilnehmer entfernen");
        alert.setHeaderText(null);
        alert.showAndWait();
        if(alert.getResult() == ButtonType.YES) {
            Persistent.members = new ArrayList<>();
            refresh_members();
        }
    }

    public void members_filter_onkeytyped() {
        String filter = members_filter.getText();
        if(filter.length() > 0) {
            members_listview.getItems().clear();
            for (DKMember m : Persistent.members) {
                String gremium = m.isGremium() ? " [" + m.getAmt() + "]" : "";
                String name = m.getName() + " (" + m.getKonvent() + ")" + gremium;
                if (name.toLowerCase().contains(filter.toLowerCase())) {
                    members_listview.getItems().add(name);
                }
            }
            members_listview.getItems().sort(Comparator.naturalOrder());
        }else {
            members_refresh_listview();
        }
    }

    private void members_refresh_listview() {
        members_listview.getItems().clear();
        for(DKMember m : Persistent.members) {
            String gremium = m.isGremium()?" [" + m.getAmt() + "]":"";
            members_listview.getItems().add(m.getName() + " (" + m.getKonvent() + ")" + gremium);
        }
        members_listview.getItems().sort(Comparator.naturalOrder());
    }






    // ==================== Seite: Druckerei ====================

    @FXML
    TextField print_filter;
    @FXML
    ListView print_listview;
    @FXML
    RadioButton print_rb_namensschild;
    @FXML
    RadioButton print_rb_meldeschild;
    @FXML
    RadioButton print_rb_gremienschild;

    public void print_export_namensschilder(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichern: Namensschilder");
        File selectedFile = fileChooser.showSaveDialog(FXMain.stage);
        if(selectedFile != null) {
            Generator.generateNamensschilder(Persistent.members, selectedFile);
        }
    }

    public void print_export_meldeschilder(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichern: Meldeschilder");
        File selectedFile = fileChooser.showSaveDialog(FXMain.stage);
        if(selectedFile != null) {
            Generator.generateMeldeschilder(Persistent.members, selectedFile);
        }
    }

    public void print_export_gremienschilder(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichern: Gremienschilder");
        File selectedFile = fileChooser.showSaveDialog(FXMain.stage);
        if(selectedFile != null) {
            Generator.generateGremienschilder(Persistent.members, selectedFile);
        }
    }

    public void print_reprint(){
        boolean namensschilder = print_rb_namensschild.isSelected();
        boolean meldeschilder = print_rb_meldeschild.isSelected();
        boolean gremienschilder = print_rb_gremienschild.isSelected();

        if(namensschilder || meldeschilder || gremienschilder) {
            //do stuff
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte wähle mindestens eine Kategorie aus!");
            alert.show();
        }
    }

    public void print_filter_onkeytyped() {
        String filter = print_filter.getText();
        if(filter.length() > 0) {
            print_listview.getItems().clear();
            for (DKMember m : Persistent.members) {
                String gremium = m.isGremium() ? " [" + m.getAmt() + "]" : "";
                String name = m.getName() + " (" + m.getKonvent() + ")" + gremium;
                if (name.toLowerCase().contains(filter.toLowerCase())) {
                    print_listview.getItems().add(name);
                }
            }
            print_listview.getItems().sort(Comparator.naturalOrder());
        }else {
            print_refresh_listview();
        }
    }

    private void print_refresh_listview() {
        print_listview.getItems().clear();
        for(DKMember m : Persistent.members) {
            String gremium = m.isGremium()?" [" + m.getAmt() + "]":"";
            print_listview.getItems().add(m.getName() + " (" + m.getKonvent() + ")" + gremium);
        }
        print_listview.getItems().sort(Comparator.naturalOrder());
    }





    // ==================== Seite: Redebeitragsstatistik ====================

    @FXML
    PieChart stats_chart_mwd;
    @FXML
    PieChart stats_chart_gremianer;
    @FXML
    CheckBox stats_cb_grafiken;
    @FXML
    CheckBox stats_cb_anzahl;
    @FXML
    CheckBox stats_cb_erlaeuterung;

    @FXML
    Label stats_l_mg, stats_l_wg, stats_l_dg;
    @FXML
    Label stats_l_ms, stats_l_ws, stats_l_ds;

    public void stats_add_mg(){
        Persistent.stat_mg++;
        stats_refresh_charts();
    }
    public void stats_add_wg(){
        Persistent.stat_wg++;
        stats_refresh_charts();
    }
    public void stats_add_dg(){
        Persistent.stat_dg++;
        stats_refresh_charts();
    }
    public void stats_add_ms(){
        Persistent.stat_ms++;
        stats_refresh_charts();
    }
    public void stats_add_ws(){
        Persistent.stat_ws++;
        stats_refresh_charts();
    }
    public void stats_add_ds(){
        Persistent.stat_ds++;
        stats_refresh_charts();
    }


    public void stats_rem_mg(){
        Persistent.stat_mg--;
        stats_refresh_charts();
    }
    public void stats_rem_wg(){
        Persistent.stat_wg--;
        stats_refresh_charts();

    }
    public void stats_rem_dg(){
        Persistent.stat_dg--;
        stats_refresh_charts();
    }
    public void stats_rem_ms(){
        Persistent.stat_ms--;
        stats_refresh_charts();
    }
    public void stats_rem_ws(){
        Persistent.stat_ws--;
        stats_refresh_charts();
    }
    public void stats_rem_ds(){
        Persistent.stat_ds--;
        stats_refresh_charts();
    }

    public void stats_export_save(){

    }

    private void stats_refresh_charts() {
        stats_chart_mwd_data = FXCollections.observableArrayList(
                new PieChart.Data("Männlich", Persistent.stat_mg + Persistent.stat_ms),
                new PieChart.Data("Weiblich", Persistent.stat_wg + Persistent.stat_ws),
                new PieChart.Data("Divers", Persistent.stat_dg + Persistent.stat_ds));
        stats_chart_mwd.setData(stats_chart_mwd_data);

        stats_chart_gremianer_data = FXCollections.observableArrayList(
                new PieChart.Data("Gremianer", Persistent.stat_mg + Persistent.stat_wg + Persistent.stat_dg),
                new PieChart.Data("Nicht-Gremianer", Persistent.stat_ms + Persistent.stat_ws + Persistent.stat_ds));
        stats_chart_gremianer.setData(stats_chart_gremianer_data);

        stats_l_mg.setText(String.valueOf(Persistent.stat_mg));
        stats_l_wg.setText(String.valueOf(Persistent.stat_wg));
        stats_l_dg.setText(String.valueOf(Persistent.stat_dg));

        stats_l_ms.setText(String.valueOf(Persistent.stat_ms));
        stats_l_ws.setText(String.valueOf(Persistent.stat_ws));
        stats_l_ds.setText(String.valueOf(Persistent.stat_ds));
    }




    // ==================== Seite: Zufallsgenerator ====================

    @FXML
    ListView random_listview_created;
    @FXML
    Label random_label_selected;
    @FXML
    ListView random_listview_selected;

    public void random_add_list(){
        try {
            URL url = new URL("file:src/de/villigst/dk/view/RandomNewListDialogView.fxml");
            Parent root = FXMLLoader.load(url);
            Stage stage = new Stage();
            stage.setTitle("Neue Liste...");
            stage.setScene(new Scene(root,250, 300));
            stage.setResizable(false);
            stage.showAndWait();

            random_refresh_list();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void random_refresh_list() {
        random_label_selected.setText("Liste:");
        random_listview_created.getItems().clear();
        for(String s : Persistent.random_lists.keySet()) {
            random_listview_created.getItems().add(s);
        }
    }

}

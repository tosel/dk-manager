package de.villigst.dk.controller;

import de.villigst.dk.FXMain;
import de.villigst.dk.logic.Generator;
import de.villigst.dk.logic.MemberImport;
import de.villigst.dk.model.DKMember;
import de.villigst.dk.persistence.ContentManager;
import de.villigst.dk.persistence.Persistent;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

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
                random_selectedList = newValue;
                if(newValue != null) {
                    random_listview_selected.getItems().clear();
                    for (DKMember m : Persistent.getRandomLists().get(newValue)) {
                        random_listview_selected.getItems().add(m.getDisplay());
                    }
                }
            }
        });

        //Initialize Context-Menu for members-listview
        members_listview.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();

            ContextMenu menu = new ContextMenu();

            MenuItem members_listview_context_edit = new MenuItem("Bearbeiten");
            members_listview_context_edit.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for(DKMember member : Persistent.getMembers()) {
                        if(cell.itemProperty().getValue().equals(member.getDisplay())) {
                            //FOUND
                            //TODO implement
                            break;
                        }
                    }
                    refresh_members();
                }
            });
            MenuItem members_listview_context_delete = new MenuItem("Entfernen");
            members_listview_context_delete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for(DKMember member : Persistent.getMembers()) {
                        if(cell.itemProperty().getValue().equals(member.getDisplay())) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Teilnehmer wirklich entfernen?", ButtonType.CANCEL, ButtonType.YES);
                            alert.setTitle("Teilnehmer entfernen");
                            alert.setHeaderText(null);
                            alert.showAndWait();
                            if(alert.getResult() == ButtonType.YES) {
                                Persistent.getMembers().remove(member);
                            }
                            break;
                        }
                    }
                    refresh_members();
                }
            });
            menu.getItems().addAll(members_listview_context_edit, members_listview_context_delete);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if(isNowEmpty) {
                    cell.setContextMenu(null);
                }else {
                    cell.setContextMenu(menu);
                }
            });

            return cell;
        });

        //Initialize Spinners from Beschlusssammlung
        ObservableList<String> items = FXCollections.observableArrayList("Frühjahrsdelegiertenkonferenz", "Herbstdelegiertenkonferenz", "Sonderdelegiertenkonferenz");
        SpinnerValueFactory<String> spinnerValueFactoryDK = new SpinnerValueFactory.ListSpinnerValueFactory<>(items);
        bs_sp_dk.setValueFactory(spinnerValueFactoryDK);

        SpinnerValueFactory<Integer> spinnerValueFactoryYear = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                Calendar.getInstance().get(Calendar.YEAR) - 5,
                Calendar.getInstance().get(Calendar.YEAR) + 5,
                Calendar.getInstance().get(Calendar.YEAR));
        bs_sp_year.setValueFactory(spinnerValueFactoryYear);
        
        
        //Initialize checkboxes for print
        print_listview.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                if(Persistent.getPrintSelected().get(item) == null) {
                    BooleanProperty prop = new SimpleBooleanProperty(false);
                    prop.addListener((obs, wasSelected, isNowSelected) -> {
                        Persistent.getPrintSelected().put(item, prop);
                    });
                    Persistent.getPrintSelected().put(item, prop);
                }
                return Persistent.getPrintSelected().get(item);
            }
        }));

        //Initialize checkboxes for random lists
        random_listview_selected.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String s) {
                if(random_selectedList == null) {
                    //keine Liste ausgewählt
                    return new SimpleBooleanProperty(false);
                }else {
                    if(Persistent.getRandomListsSelected().get(random_selectedList).get(s) == null) {
                        Persistent.getRandomListsSelected().get(random_selectedList).put(s, new SimpleBooleanProperty(false));
                    }
                }
                return Persistent.getRandomListsSelected().get(random_selectedList).get(s);
            }
        }));
    }



    private void refresh_members() {
        Collections.sort(Persistent.getMembers());
        members_refresh_listview();
        print_refresh_listview();
    }

    // ==================== Seite: ÜBERSICHT ====================

    @FXML
    TextArea overview_ta_notes;

    public void overview_ta_onkeytyped() {
        Persistent.setNotes(overview_ta_notes.getText());
    }

    public void overview_reset_content() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Wirklich alle Inhalte zurücksetzen?", ButtonType.CANCEL, ButtonType.YES);
        alert.setTitle("Inhalte zurücksetzen");
        alert.setHeaderText(null);
        alert.showAndWait();
        if(alert.getResult() == ButtonType.YES) {
            ContentManager.reset();
            overview_ta_notes.setText(Persistent.getNotes());
            refresh_members();
            stats_refresh_charts();
        }
    }



    // ==================== Seite: Teilnehmerverwaltung ====================

    @FXML
    TextField members_filter;
    @FXML
    ListView members_listview;
    @FXML
    AnchorPane members_anchor;
    //@FXML
    //ContextMenu members_listview_context;

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
            stage.setScene(new Scene(root, 300, 450));
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
            Persistent.setMembers(new ArrayList<>());
            refresh_members();
        }
    }

    public void members_filter_onkeytyped() {
        String filter = members_filter.getText();
        if(filter.length() > 0) {
            members_listview.getItems().clear();
            for (DKMember m : Persistent.getMembers()) {
                if (m.getDisplay().toLowerCase().contains(filter.toLowerCase())) {
                    members_listview.getItems().add(m.getDisplay());
                }
            }
        }else {
            members_refresh_listview();
        }
    }

    private void members_refresh_listview() {
        members_listview.getItems().clear();
        for(DKMember m : Persistent.getMembers()) {
            members_listview.getItems().add(m.getDisplay());
        }
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
            if(Persistent.getMembers().size() > 0) {
                Generator.generateNamensschilder(Persistent.getMembers(), selectedFile);
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText(null);
                alert.setContentText("Keine Teilnehmer zum Drucken vorhanden!");
                alert.show();
            }
        }
    }

    public void print_export_meldeschilder(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichern: Meldeschilder");
        File selectedFile = fileChooser.showSaveDialog(FXMain.stage);
        if(selectedFile != null) {
            if(Persistent.getMembers().size() > 0) {
                Generator.generateMeldeschilder(Persistent.getMembers(), selectedFile);
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText(null);
                alert.setContentText("Keine Teilnehmer zum Drucken vorhanden!");
                alert.show();
            }
        }
    }

    public void print_export_gremienschilder(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichern: Gremienschilder");
        File selectedFile = fileChooser.showSaveDialog(FXMain.stage);
        if(selectedFile != null) {
            if(Persistent.getMembers().size() > 0) {
                Generator.generateGremienschilder(Persistent.getMembers(), selectedFile);
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText(null);
                alert.setContentText("Keine Teilnehmer zum Drucken vorhanden!");
                alert.show();
            }
        }
    }

    public void print_reprint(){
        boolean namensschilder = print_rb_namensschild.isSelected();
        boolean meldeschilder = print_rb_meldeschild.isSelected();
        boolean gremienschilder = print_rb_gremienschild.isSelected();

        if(namensschilder || meldeschilder || gremienschilder) {
            List<DKMember> toReprint = new ArrayList<>();
            //unschön...
            for(String s : Persistent.getPrintSelected().keySet()) {
                if(Persistent.getPrintSelected().get(s).getValue()) {
                    for(DKMember m : Persistent.getMembers()) {
                        if(m.getDisplay().equals(s)) {
                            toReprint.add(m);
                        }
                    }
                }
            }

            if(!toReprint.isEmpty()) {

                if(gremienschilder) {
                    for(DKMember m : toReprint) {
                        if(!m.isGremium()) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Fehler");
                            alert.setHeaderText(null);
                            alert.setContentText("Gremienschilder können nur für Gremianer erstellt werden!");
                            alert.show();
                            return;
                        }
                    }
                }

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Beschlusssammlung speichern");
                File selectedFile = fileChooser.showSaveDialog(FXMain.stage);

                if (selectedFile != null) {
                    if (namensschilder)
                        Generator.generateNamensschilder(toReprint, selectedFile);
                    if (meldeschilder)
                        Generator.generateMeldeschilder(toReprint, selectedFile);
                    if (gremienschilder) {
                        Generator.generateGremienschilder(toReprint, selectedFile);
                    }
                }
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText(null);
                alert.setContentText("Bitte wähle mindestens einen Teilnehmer aus!");
                alert.show();
            }

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
            for (DKMember m : Persistent.getMembers()) {
                if (m.getDisplay().toLowerCase().contains(filter.toLowerCase())) {
                    print_listview.getItems().add(m.getDisplay());
                }
            }
        }else {
            print_refresh_listview();
        }
    }

    private void print_refresh_listview() {
        print_listview.getItems().clear();
        for(DKMember m : Persistent.getMembers()) {
            print_listview.getItems().add(m.getDisplay());
        }
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
    GridPane stats_table;
    @FXML
    Button stats_btn_edit;

    @FXML
    Label stats_l_mg, stats_l_wg, stats_l_dg;
    @FXML
    Label stats_l_ms, stats_l_ws, stats_l_ds;
    @FXML
    TextField stats_tf_mg, stats_tf_wg, stats_tf_dg;
    @FXML
    TextField stats_tf_ms, stats_tf_ws, stats_tf_ds;

    public void stats_add_mg(){
        Persistent.setStatMG(Persistent.getStatMG() + 1);
        stats_refresh_charts();
    }
    public void stats_add_wg(){
        Persistent.setStatWG(Persistent.getStatWG() + 1);
        stats_refresh_charts();
    }
    public void stats_add_dg(){
        Persistent.setStatDG(Persistent.getStatDG() + 1);
        stats_refresh_charts();
    }
    public void stats_add_ms(){
        Persistent.setStatMS(Persistent.getStatMS() + 1);
        stats_refresh_charts();
    }
    public void stats_add_ws(){
        Persistent.setStatWS(Persistent.getStatWS() + 1);
        stats_refresh_charts();
    }
    public void stats_add_ds(){
        Persistent.setStatDS(Persistent.getStatDS() + 1);
        stats_refresh_charts();
    }


    public void stats_rem_mg(){
        Persistent.setStatMG(Persistent.getStatMG()>0?Persistent.getStatMG() - 1:0);
        stats_refresh_charts();
    }
    public void stats_rem_wg(){
        Persistent.setStatWG(Persistent.getStatMG()>0?Persistent.getStatWG() - 1:0);
        stats_refresh_charts();
    }
    public void stats_rem_dg(){
        Persistent.setStatDG(Persistent.getStatMG()>0?Persistent.getStatDG() - 1:0);
        stats_refresh_charts();
    }
    public void stats_rem_ms(){
        Persistent.setStatMS(Persistent.getStatMG()>0?Persistent.getStatMS() - 1:0);
        stats_refresh_charts();
    }
    public void stats_rem_ws(){
        Persistent.setStatWS(Persistent.getStatMG()>0?Persistent.getStatWS() - 1:0);
        stats_refresh_charts();
    }
    public void stats_rem_ds(){
        Persistent.setStatDS(Persistent.getStatMG()>0?Persistent.getStatDS() - 1:0);
        stats_refresh_charts();
    }

    public void stats_export_save(){

    }

    public void stats_edit() {
        if(stats_btn_edit.getText().equals("Bearbeiten")) {
            //Set Text-Fields visible
            stats_tf_mg.setVisible(true);
            stats_tf_wg.setVisible(true);
            stats_tf_dg.setVisible(true);
            stats_tf_ms.setVisible(true);
            stats_tf_ws.setVisible(true);
            stats_tf_ds.setVisible(true);

            //Put values in
            stats_tf_mg.setText(stats_l_mg.getText());
            stats_tf_wg.setText(stats_l_wg.getText());
            stats_tf_dg.setText(stats_l_dg.getText());
            stats_tf_ms.setText(stats_l_ms.getText());
            stats_tf_ws.setText(stats_l_ws.getText());
            stats_tf_ds.setText(stats_l_ds.getText());

            stats_btn_edit.setText("Speichern");
        }else if(stats_btn_edit.getText().equals("Speichern")) {
            try {
                //Save values
                Persistent.setStatMG(Integer.valueOf(stats_tf_mg.getText()));
                Persistent.setStatWG(Integer.valueOf(stats_tf_wg.getText()));
                Persistent.setStatDG(Integer.valueOf(stats_tf_dg.getText()));
                Persistent.setStatMS(Integer.valueOf(stats_tf_ms.getText()));
                Persistent.setStatWS(Integer.valueOf(stats_tf_ws.getText()));
                Persistent.setStatDS(Integer.valueOf(stats_tf_ds.getText()));
            }catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText(null);
                alert.setContentText("Bitte gib nur Zahlen an!");
                alert.show();
                return;
            }
            //Put values in
            stats_l_mg.setText(String.valueOf(Persistent.getStatMG()));
            stats_l_wg.setText(String.valueOf(Persistent.getStatWG()));
            stats_l_dg.setText(String.valueOf(Persistent.getStatDG()));
            stats_l_ms.setText(String.valueOf(Persistent.getStatMS()));
            stats_l_ws.setText(String.valueOf(Persistent.getStatWS()));
            stats_l_ds.setText(String.valueOf(Persistent.getStatDS()));

            //Set Text-Fields visible
            stats_tf_mg.setVisible(false);
            stats_tf_wg.setVisible(false);
            stats_tf_dg.setVisible(false);
            stats_tf_ms.setVisible(false);
            stats_tf_ws.setVisible(false);
            stats_tf_ds.setVisible(false);

            stats_refresh_charts();

            stats_btn_edit.setText("Bearbeiten");
        }

    }

    private void stats_refresh_charts() {
        stats_chart_mwd_data = FXCollections.observableArrayList(
                new PieChart.Data("Männlich", Persistent.getStatMG() + Persistent.getStatMS()),
                new PieChart.Data("Weiblich", Persistent.getStatWG() + Persistent.getStatWS()),
                new PieChart.Data("Divers", Persistent.getStatDG() + Persistent.getStatDS()));
        stats_chart_mwd.setData(stats_chart_mwd_data);

        stats_chart_gremianer_data = FXCollections.observableArrayList(
                new PieChart.Data("Gremianer", Persistent.getStatMG() + Persistent.getStatWG() + Persistent.getStatDG()),
                new PieChart.Data("Nicht-Gremianer", Persistent.getStatMS() + Persistent.getStatWS() + Persistent.getStatDS()));
        stats_chart_gremianer.setData(stats_chart_gremianer_data);

        stats_l_mg.setText(String.valueOf(Persistent.getStatMG()));
        stats_l_wg.setText(String.valueOf(Persistent.getStatWG()));
        stats_l_dg.setText(String.valueOf(Persistent.getStatDG()));

        stats_l_ms.setText(String.valueOf(Persistent.getStatMS()));
        stats_l_ws.setText(String.valueOf(Persistent.getStatWS()));
        stats_l_ds.setText(String.valueOf(Persistent.getStatDS()));
    }




    // ==================== Seite: Zufallsgenerator ====================

    @FXML
    ListView random_listview_created;
    @FXML
    Label random_label_selected;
    @FXML
    ListView random_listview_selected;

    String random_selectedList;

    public void random_add_list(){
        try {
            URL url = new URL("file:src/de/villigst/dk/view/RandomNewListDialogView.fxml");
            Parent root = FXMLLoader.load(url);
            Stage stage = new Stage();
            stage.setTitle("Neue Liste...");
            stage.setScene(new Scene(root,300, 450));
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
        for(String s : Persistent.getRandomLists().keySet()) {
            random_listview_created.getItems().add(s);
        }
    }


    // ==================== Seite: Beschlusssammlung ====================

    @FXML
    Spinner bs_sp_dk, bs_sp_year;

    @FXML
    TextField bs_tf_page;

    @FXML
    Button bs_btn_contents, bs_btn_lastBS, bs_btn_newBS;

    File contents, lastBS, newBS;

    public void bs_choose_contents() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Inhaltsverzeichnis auswählen...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(FXMain.stage);
        if(selectedFile != null) {
            contents = selectedFile;
            bs_btn_contents.setText(selectedFile.getName());
        }

    }

    public void bs_choose_lastversion() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Letzte Beschlusssammlung auswählen...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(FXMain.stage);
        if(selectedFile != null) {
            lastBS = selectedFile;
            bs_btn_lastBS.setText(selectedFile.getName());
        }
    }

    public void bs_choose_newbs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Neue Beschlüsse auswählen...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(FXMain.stage);
        if(selectedFile != null) {
            newBS = selectedFile;
            bs_btn_newBS.setText(selectedFile.getName());
        }
    }

    public void bs_generate() {
        if(contents == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte gib ein Inhaltsverzeichnis an!");
            alert.show();
        }else if(lastBS == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte gib die letzte Beschlusssammlung an!");
            alert.show();
        }else if(newBS == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte gib die Beschlüsse der aktuellen DK an!");
            alert.show();
        }else if(bs_tf_page.getText().isBlank()) {
            //TODO test: isBlank()
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Bitte gib die erste Seite mit Beschlüssen an!");
            alert.show();
        }else {
            int offset;
            try {
                offset = Integer.valueOf(bs_tf_page.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText(null);
                alert.setContentText("Bitte gib eine gültige Seitenzahl an!");
                alert.show();
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Beschlusssammlung speichern");
            File selectedFile = fileChooser.showSaveDialog(FXMain.stage);

            if (selectedFile != null) {
                //Generate!
                String title = "Beschlusssammlung\n" + bs_sp_dk.getPromptText() + " " + bs_sp_year.getPromptText();
                Generator.generateBeschlusssammlung(selectedFile, title, contents, lastBS, newBS, offset);
                //Reset Buttons
                bs_btn_contents.setText("PDF Datei auswählen...");
                bs_btn_lastBS.setText("PDF Datei auswählen...");
                bs_btn_newBS.setText("PDF Datei auswählen...");
                bs_tf_page.setText("");
            }
        }
    }

}

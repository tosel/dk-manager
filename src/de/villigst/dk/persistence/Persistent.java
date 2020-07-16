package de.villigst.dk.persistence;

import de.villigst.dk.model.DKMember;
import javafx.beans.property.BooleanProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * Enthält alle Variablen, die gespeichert werden
 */
public class Persistent {
    //Zu Speichern:
    private static List<DKMember> members = new ArrayList<>();

    private static int stat_mg = 0;
    private static int stat_wg = 0;
    private static int stat_dg = 0;

    private static int stat_ms = 0;
    private static int stat_ws = 0;
    private static int stat_ds = 0;

    private static HashMap<String, List<DKMember>> random_lists = new HashMap<>();
    private static HashMap<String, HashMap<String, BooleanProperty>> random_lists_selected = new HashMap<>();

    private static HashMap<String, BooleanProperty> print_selected = new HashMap<>();

    private static String notes = "";


    public static List<DKMember> getMembers() {
        return members;
    }

    public static void setMembers(List<DKMember> members) {
        Persistent.members = members;
        ContentManager.onValueChange();
    }

    public static int getStatMG() {
        return stat_mg;
    }

    public static void setStatMG(int stat_mg) {
        Persistent.stat_mg = stat_mg;
        ContentManager.onValueChange();
    }

    public static int getStatWG() {
        return stat_wg;
    }

    public static void setStatWG(int stat_wg) {
        Persistent.stat_wg = stat_wg;
        ContentManager.onValueChange();
    }

    public static int getStatDG() {
        return stat_dg;
    }

    public static void setStatDG(int stat_dg) {
        Persistent.stat_dg = stat_dg;
        ContentManager.onValueChange();
    }

    public static int getStatMS() {
        return stat_ms;
    }

    public static void setStatMS(int stat_ms) {
        Persistent.stat_ms = stat_ms;
        ContentManager.onValueChange();
    }

    public static int getStatWS() {
        return stat_ws;
    }

    public static void setStatWS(int stat_ws) {
        Persistent.stat_ws = stat_ws;
        ContentManager.onValueChange();
    }

    public static int getStatDS() {
        return stat_ds;
    }

    public static void setStatDS(int stat_ds) {
        Persistent.stat_ds = stat_ds;
        ContentManager.onValueChange();
    }

    public static HashMap<String, List<DKMember>> getRandomLists() {
        return random_lists;
    }

    public static void setRandomLists(HashMap<String, List<DKMember>> random_lists) {
        Persistent.random_lists = random_lists;
        ContentManager.onValueChange();
    }

    public static HashMap<String, HashMap<String, BooleanProperty>> getRandomListsSelected() {
        return random_lists_selected;
    }

    public static void setRandomListsSelected(HashMap<String, HashMap<String, BooleanProperty>> random_lists_selected) {
        Persistent.random_lists_selected = random_lists_selected;
        ContentManager.onValueChange();
    }

    public static HashMap<String, BooleanProperty> getPrintSelected() {
        return print_selected;
    }

    public static void setPrintSelected(HashMap<String, BooleanProperty> print_selected) {
        Persistent.print_selected = print_selected;
        ContentManager.onValueChange();
    }

    public static String getNotes() {
        return notes;
    }

    public static void setNotes(String notes) {
        Persistent.notes = notes;
        ContentManager.onValueChange();
    }
}

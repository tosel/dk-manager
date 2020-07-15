package de.villigst.dk.persistence;

import de.villigst.dk.model.DKMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * Enthält alle Variablen, die gespeichert werden
 */
public class Persistent {
    //Constanten
    public static final String OUTPUT_PATH = "out/";


    //Zu Speichern:
    public static List<DKMember> members = new ArrayList<>();

    public static int stat_mg = 0;
    public static int stat_wg = 0;
    public static int stat_dg = 0;

    public static int stat_ms = 0;
    public static int stat_ws = 0;
    public static int stat_ds = 0;

    public static HashMap<String, List<DKMember>> random_lists = new HashMap<>();


}

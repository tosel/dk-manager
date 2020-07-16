package de.villigst.dk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class DKMember implements Comparable<DKMember> {

    private String name, konvent, amt;

    public DKMember(@NotNull String name, @NotNull String konvent, String amt) {
        this.name = name;
        this.konvent = konvent;
        this.amt = amt;
    }

    @JsonCreator
    public DKMember(@JsonProperty("name") String name, @JsonProperty("konvent") String konvent, @JsonProperty("amt") String amt, @JsonProperty("gremium") boolean gremium) {
        this.name = name;
        this.konvent = konvent;
        this.amt = amt;
    }

    @Override
    public int compareTo(@NotNull DKMember compareTo) {
        String compare = compareTo.getDisplay();
        String here = this.getDisplay();
        return here.compareTo(compare);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKonvent() {
        return konvent;
    }

    public void setKonvent(String konvent) {
        this.konvent = konvent;
    }

    public String getAmt() {
        return konvent.equalsIgnoreCase("Programmausschuss")||konvent.equalsIgnoreCase("PA")?"PA-Mitglied":amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public boolean isGremium() {
        //Threshold für Nullbyte/Zeilenumburch/etc.
        return getAmt().length() > 4 || konvent.equalsIgnoreCase("Programmausschuss") || konvent.equalsIgnoreCase("PA");
    }

    public boolean isPA() {
        return konvent.equalsIgnoreCase("Programmausschuss") || konvent.equalsIgnoreCase("PA");
    }

    public boolean isSenat() {
        //Threshold für Nullbyte/Zeilenumburch/etc.
        return getAmt().length() > 4 && !(getAmt().contains("PA") || getAmt().contains("Programmausschuss"));
    }

    public boolean isGS() {
        return konvent.equalsIgnoreCase("Geschäftsstelle")
                || konvent.equalsIgnoreCase("GS");
    }

    public boolean isGuest() {
        return konvent.equalsIgnoreCase("Cusanus")
                || konvent.equalsIgnoreCase("ESG")
                || konvent.equalsIgnoreCase("ELES")
                || konvent.equalsIgnoreCase("HBS")
                || konvent.equalsIgnoreCase("Hans-Böckler")
                || konvent.equalsIgnoreCase("Hans Böckler")
                || konvent.equalsIgnoreCase("Heinrich-Böll")
                || konvent.equalsIgnoreCase("Heinrich Böll")
                || konvent.equalsIgnoreCase("Studienstiftung")
                || konvent.equalsIgnoreCase("KAS")
                || konvent.equalsIgnoreCase("FES")
                || konvent.equalsIgnoreCase("SDW")
                || konvent.equalsIgnoreCase("FNS")
                || konvent.equalsIgnoreCase("HNS")
                || konvent.equalsIgnoreCase("RLS")
                || konvent.equalsIgnoreCase("RoLux")
                || konvent.equalsIgnoreCase("Avicenna");
    }

    @Override
    public String toString() {
        return "DKMember{" +
                "name='" + name + '\'' +
                ", konvent='" + konvent + '\'' +
                ", amt=" + amt +
                ", gremium=" + isGremium() +
                ", guest=" + isGuest() +
                ", gs=" + isGS() +
                '}';
    }

    public String getDisplay() {
        String amt = isGremium()?" [" + getAmt() + "]":"";
        return name + " (" + konvent + ")" + amt;
    }
}

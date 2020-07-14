package de.villigst.dk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class DKMember {

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
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public boolean isGremium() {
        //Threshold für Nullbyte/Zeilenumburch/etc.
        return getAmt().length() > 4;
    }

    @Override
    public String toString() {
        return "DKMember{" +
                "name='" + name + '\'' +
                ", konvent='" + konvent + '\'' +
                ", amt=" + amt +
                ", gremium=" + isGremium() +
                '}';
    }
}

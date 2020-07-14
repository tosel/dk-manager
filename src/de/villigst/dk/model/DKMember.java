package de.villigst.dk.model;

public class DKMember {

    private String name, konvent;
    private boolean gremium;

    public DKMember(String name, String konvent, boolean gremium) {
        this.name = name;
        this.konvent = konvent;
        this.gremium = gremium;
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

    public boolean isGremium() {
        return gremium;
    }

    public void setGremium(boolean gremium) {
        this.gremium = gremium;
    }
}

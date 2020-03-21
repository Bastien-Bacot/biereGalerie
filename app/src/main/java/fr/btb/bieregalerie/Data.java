package fr.btb.bieregalerie;

import java.io.Serializable;

public class Data implements Serializable {

    private int rat;
    private String com, name, cat, type;
    boolean bcom;

    public Data(){}

    public Data ( String cat, String name, int rat, String com, String type){
        this.cat = cat;
        this.name = name;
        this.rat = rat;
        this.com = com;
        this.bcom = true;
        this.type = type;
    }
    public Data ( String cat, String name, int rat, String type){
        this.cat = cat;
        this.name = name;
        this.rat = rat;
        this.bcom = false;
        this.type = type;
    }
    public Data (String cat, String name, String com, String type){
        this.rat = -1;
        this.com = com;
        this.name = name;
        this.cat = cat;
        this.bcom = true;
        this.type = type;
    }
    public Data (String cat, String name, String type){
        this.rat = -1;
        this.name = name;
        this.cat = cat;
        this.bcom = false;
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public int getRat() {
        return rat;
    }

    public String getCom() {
        return com;
    }

    public String getName() {
        return name;
    }

    public String getCat() {
        return cat;
    }

    public void setRat(int rat) {
        this.rat = rat;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public boolean getBcom() {
        return bcom;
    }

    public void setBcom(boolean bcom) {
        this.bcom = bcom;
    }



    @Override
    public String toString() {
        return cat
                +","+name
                +","+rat
                +","+com
                +","+type;
    }
}
